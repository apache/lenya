
function tab() {

    var where = cocoon.parameters["where"];
    var r = cocoon.parameters["radius"];
    var x;
    var y;
    var width;
    var height = 40;
    
    if (where == "topLeft") {
        x = r;
        y = r;
        width = 300;
    }
    else if (where == "topRight") {
        x = -1;
        y = r;
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