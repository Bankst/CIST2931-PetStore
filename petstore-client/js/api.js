function sendRequest(endpoint, request, data, onSuccess, onError) {
    const XHR = new XMLHttpRequest();

    XHR.addEventListener('load', onSuccess);
    XHR.addEventListener('error', onError);

    XHR.open(request, endpoint);
    XHR.send(data);
}