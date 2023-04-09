import axios, { AxiosInstance } from "axios";
import FormData from "form-data";
import { wrapper } from 'axios-cookiejar-support';
import { CookieJar } from 'tough-cookie';

export async function getClient() {
    const jar = new CookieJar();
    const client = wrapper(axios.create({
        jar,
        baseURL: process.env.API_ASSETSFIRST_URL,
        withCredentials: true,
        headers: {'X-Requested-With': 'XMLHttpRequest'},
    }));

    const token = await getInitialCsrfToken(client);
    const authData = new FormData();
    authData.append('username', process.env.API_ASSETSFIRST_USERNAME);
    authData.append('password', process.env.API_ASSETSFIRST_PASSWORD);
    try {
        const loginResponse = await client.post("/auth/login", authData, {
            headers: {
                'X-XSRF-TOKEN': token,
                ...authData.getHeaders()
            }
        });
        const loggedCookie: any = loginResponse.headers['set-cookie'];
        client.defaults.headers.common['X-XSRF-TOKEN'] = getCsrfTokenFromCookie(loggedCookie[1]);
    } catch (e: any) {
        console.log(e.config.data)
    }
    return client;
}

async function getInitialCsrfToken(client: AxiosInstance) {
    try {
        await client.get('/auth/user');
    } catch (e: any) {
        if (!e.response) {
            throw e.message;
        }
        const cookies = e.response.headers['set-cookie'];
        return getCsrfTokenFromCookie(cookies[0]);
    }
    return '';
}

function getCsrfTokenFromCookie(cookie: string): string {
    const xsrfCookies = cookie.split(';')
        .map(c => c.trim())
        .filter(c => c.startsWith('XSRF-TOKEN='));

    if (xsrfCookies.length === 0) {
        return '';
    }
    return xsrfCookies[0].split('=')[1];
}
