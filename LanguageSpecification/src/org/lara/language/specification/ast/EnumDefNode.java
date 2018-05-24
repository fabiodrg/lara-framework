/**
 * Copyright 2016 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.language.specification.ast;

import pt.up.fe.specs.util.utilities.BuilderWithIndentation;

public class EnumDefNode extends LangSpecNode {

    private final String name;

    public EnumDefNode(String name) {
        this.name = name;

    }

    @Override
    public String toContentString() {
        return "name: " + getName();
    }

    @Override
    public String toJson(BuilderWithIndentation builder) {
        builder.addLines("{");
        builder.increaseIndentation();
        builder.addLines("\"type\": \"enum\",");
        builder.addLines("\"name\": \"" + getName() + "\"");
        if (this.hasChildren()) {
            builder.addLine(",");
            builder.addLines(childrenToJson(builder.getCurrentIdentation() + 1));
        }
        builder.decreaseIndentation();
        builder.add("}");
        return builder.toString();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toHtml() {
        StringBuilder html = new StringBuilder();

        // Using styles from LaraDoc
        html.append("<div id='toc_container'>");

        html.append("<p class='toc_title'>").append(name).append("</p>");

        html.append("</div>");

        return html.toString();
    }
}
