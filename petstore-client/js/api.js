function sendRequest(endpoint, request, data, onSuccess, onError) {
    const XHR = new XMLHttpRequest();

    XHR.addEventListener('load', onSuccess);
    XHR.addEventListener('error', onError);

    XHR.open(request, endpoint);
    XHR.send(data);
}

Number.prototype.pad = function(size) {
    let s = String(this);
    while (s.length < (size || 2)) {s = "0" + s;}
    return s;
}