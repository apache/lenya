
function tab() {

    var where = cocoon.parameters["where"];
    var r = cocoon.parameters["radius"];
    var x;
    var y;
    var width;
    var height = 50;
    var maxWidth = 1500;
    
    if (where == "topLeft") {
        x = r;
        y = r;
        width = maxWidth;
    }
    else if (where == "topRight") {
        x = -1;
        y = r;
        width = r;
    }
    else if (where == "bottomLeft") {
        x = r;
        y = height - r - 1;
        width = maxWidth;
    }
    else if (where == "bottomRight") {
        x = -1;
        y = height - r - 1;
        width = r;
    }

    cocoon.sendPage("view/tab", {
        "backgroundColor" : cocoon.parameters["backgroundColor"],
        "lineColor" : cocoon.parameters["lineColor"],
        "where" : where,
        "x" : x,
        "y" : y,
        "r" : r,
        "width" : width,
        "height" : height
    });

}