
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