import axios from "axios";
import UserAgent from "user-agents";

export const getParserClient = () => {
    return axios.create({
        headers: {
            'User-Agent': new UserAgent().toString(),
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
            'Accept-Language': 'en-US,en;q=0.5',
            'Accept-Encoding': 'gzip, deflate, br',
            'Connection': 'keep-alive',
            'Upgrade-Insecure-Requests': '1'
        },
    })
}