import axios from "axios";
import FormData from "form-data";
import {wrapper} from 'axios-cookiejar-support';
import {CookieJar} from 'tough-cookie';

export async function getClient() {
    const jar = new CookieJar();
    const client = wrapper(axios.create({
        jar,
        baseURL: 'http://localhost:8080',
        withCredentials: true,
        headers: {'X-Requested-With': 'XMLHttpRequest'},
    }));
    let token;
    try {
        await client.get('/auth/user');
    } catch (e) {
        const cookies = e.response.headers['set-cookie'];
        token = getCsrfToken(cookies[0]);
    }
    const authData = new FormData();
    authData.append('username', 'user');
    authData.append('password', '&}vU6Nw6');
    try {
        const loginResponse = await client.post("/auth/login", authData, {
            headers: {
                'X-XSRF-TOKEN': token,
                ...authData.getHeaders()
            }
        });
        const loggedCookie = loginResponse.headers['set-cookie'];
        client.defaults.headers['X-XSRF-TOKEN'] = getCsrfToken(loggedCookie[1]);
    } catch (e) {
        console.log(e.config.data)
    }
    return client;
}

export function getCsrfToken(cookie) {
    const xsrfCookies = cookie.split(';')
        .map(c => c.trim())
        .filter(c => c.startsWith('XSRF-TOKEN='));

    if (xsrfCookies.length === 0) {
        return null;
    }
    return xsrfCookies[0].split('=')[1];
}
