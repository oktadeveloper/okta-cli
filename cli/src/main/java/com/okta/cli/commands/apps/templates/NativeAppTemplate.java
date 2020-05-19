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
package com.okta.cli.commands.apps.templates;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum NativeAppTemplate {

    GENERIC("Native App");

    private static final List<String> names = Arrays.stream(values()).map(it -> it.friendlyName).collect(Collectors.toList());

    private final String friendlyName;

    NativeAppTemplate(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    static NativeAppTemplate fromName(String name) {
        return Arrays.stream(values())
                .filter(it -> it.friendlyName.equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("template must be empty or one of: " + names));
    }
}