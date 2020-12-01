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

function setClientAccountStatus() {
    if (localStorage.getItem('first_name') !== null) {
        document.getElementById("status").innerHTML = '<a href="#" onclick="logOut()">Sign out</a>';
    } else {
        document.getElementById("status").innerHTML = '<a href="/login.html">Sign in</a>';
    }
    document.getElementById("cart_button").innerHTML = `<a class="nav-link" href="${getCartUrl()}"><i class="fa nav__icon">&#xf07a;</i></a>`;
}

function getCartUrl() {
    return localStorage.getItem('first_name') !== null ? "/customercart.html" : "/guestcart.html";
}

function logOut(){
    sendRequest('/api/v1/customer/logout', 'POST', null, function () {
        if (this.status === 200) {
            unsetCustomerData();
            document.location.assign("index.html");
        } else {
            switch (this.status) {
                case 401:
                    // unset the data because they are already logged out in the backend
                    unsetCustomerData();
                    document.location.assign("index.html");
                    break;
                default:
                    generalError(`Error ${this.status} when logging out`);
                    break;
            }
        }
    }, generalError);
}

function logOutEmployee() {
    sendRequest('/api/v1/employee/logout', 'POST', null, function () {
        if (this.status === 200) {
            unsetEmployeeData();
            document.location.assign("/employee/login.html");
        } else {
            switch (this.status) {
                case 401:
                    // unset the data because they are already logged out in the backend
                    unsetEmployeeData();
                    document.location.assign("/employee/login.html");
                    break;
                default:
                    generalError(`Error ${this.status} when logging out`);
                    break;
            }
        }
    }, generalError);
}

function unsetCustomerData() {
    localStorage.removeItem("first_name");
    localStorage.removeItem("last_name");
    localStorage.removeItem("street");
    localStorage.removeItem("city");
    localStorage.removeItem("state");
    localStorage.removeItem("zip");
    localStorage.removeItem("email");
    localStorage.removeItem("phone");
}

function unsetEmployeeData() {
    localStorage.removeItem('emp_username');
    localStorage.removeItem('emp_name');
}


function generalError(x) {
    console.error(x);
}
