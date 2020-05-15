/*
 * Copyright 2020-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.cli.test

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.testng.annotations.Test

import static com.okta.cli.test.CommandRunner.resultMatches
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.allOf
import static org.hamcrest.Matchers.containsString

class AppsCreateIT implements MockWebSupport, CreateAppSupport {

    @Test
    void createSpaApp() {
        MockWebServer mockWebServer = createMockServer()
        List<MockResponse> responses = [
                                        // GET /api/v1/authorizationServers
                                        jsonRequest('[{ "id": "test-as", "name": "test-as-name", "issuer": "' + mockWebServer.url("/") + '/oauth2/test-as" }]'),
                                        // GET /api/v1/apps?q=integration-tests
                                        jsonRequest('[]'),
                                        // POST /api/v1/apps
                                        jsonRequest('{ "id": "test-app-id", "label": "test-app-name" }'),
                                        // GET /api/v1/groups?q=everyone
                                        jsonRequest("[${everyoneGroup()}]"),
                                        // PUT /api/v1/apps/test-app-id/groups/every1-id
                                        jsonRequest('{}'),
                                        //GET /api/v1/internal/apps/test-app-id/settings/clientcreds
                                        jsonRequest('{ "client_id": "test-id" }')]

        mockWebServer.with {
            responses.forEach { mockWebServer.enqueue(it) }

            List<String> input = [
                "2", // type of app choice
                "",  // default of "test-project"
                "",  // default callback "http://localhost:8080/callback"
            ]

            def result = new CommandRunner()
                    .withSdkConfig(mockWebServer.url("/").toString())
                    .runCommandWithInput(input,"--color=never", "apps", "create")


            assertThat result, resultMatches(0, allOf(
                                                            containsString("Okta application configuration:"),
                                                            containsString("okta.oauth2.client-id: test-id"),
                                                            containsString("okta.oauth2.issuer: ${mockWebServer.url("/")}/oauth2/test-as")),
                                        null)
        }
    }
}
