// import {fetch, CookieJar} from 'node-fetch-cookies';
import fetch, {FormData} from 'node-fetch';


export async function authenticate() {
    const userResponse = await fetch('http://localhost:8080/auth/user');
    const cookies = userResponse.headers.raw()['set-cookie'];

    const authData = new FormData();
    authData.set('username', 'user');
    authData.set('password', '&}vU6Nw6');
    const token = getCsrfToken(cookies[0]);
    const headers = {
        'cookie': cookies.join(";"),
        'X-XSRF-TOKEN': token
    };
    const loginResponse = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        body: authData,
        headers
    });
    return  {
        'cookie': loginResponse.headers.raw()['set-cookie'].join(";"),
        'X-XSRF-TOKEN': token
    };
}

function getCsrfToken(cookie) {
    const xsrfCookies = cookie.split(';')
        .map(c => c.trim())
        .filter(c => c.startsWith('XSRF-TOKEN='));

    if (xsrfCookies.length === 0) {
        return null;
    }
    return xsrfCookies[0].split('=')[1];
}
