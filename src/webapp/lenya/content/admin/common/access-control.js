
function getUserManager() {
    return getManager("user");
}

function getGroupManager() {
    return getManager("group");
}

function getIPRangeManager() {
    return getManager("iprange");
}

function getManager(type) {
    var manager = cocoon.inputModuleGetAttribute("access-control", type + "-manager");
    return manager;
}