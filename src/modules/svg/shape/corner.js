/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

function corner() {

    var where = cocoon.parameters["where"];
    var r = cocoon.parameters["radius"];
    var x;
    var y;
    var width;
    var height;
    
    if (where == "topLeft") {
        x = r;
        y = r;
        width = r;
        height = r;
    }
    else if (where == "topRight") {
        x = 0;
        y = r;
        width = r;
        height = r;
    }
    else if (where == "bottomLeft") {
        x = r;
        y = 0;
        width = r;
        height = r;
    }
    else if (where == "bottomRight") {
        x = 0;
        y = 0;
        width = r;
        height = r;
    }
    else if (where == "top") {
        x = r;
        y = 0;
        width = 1;
        height = r;
    }
    else if (where == "right") {
        x = 0;
        y = r;
        width = r;
        height = 1;
    }
    else if (where == "left") {
        x = r;
        y = 0;
        width = r;
        height = 1;
    }
    else if (where == "bottom") {
        x = 0;
        y = 0;
        width = 1;
        height = r;
    }

    cocoon.sendPage("view/corner", {
        "backgroundColor" : cocoon.parameters["backgroundColor"],
        "lineColor" : cocoon.parameters["lineColor"],
        "x" : x,
        "y" : y,
        "r" : r,
        "width" : width,
        "height" : height
    });

}
