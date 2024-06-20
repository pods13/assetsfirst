import {format, parse} from "fast-csv";
import fs from "fs";
import {unlink} from "fs/promises";
import manualData from './data/etf/add.json';

export async function collectEtfs(country: string, exchanges: string[] = []) {
    if (!exchanges.length) {
        return await collectEtfsByExchange(country, null);
    }
    const whenEtfsCollected = exchanges.map(exchange => collectEtfsByExchange(country, exchange));
    const filenames = await Promise.allSettled(whenEtfsCollected);
    const mainStream = format({headers: true});
    const filename = composeFilename(country);
    mainStream.pipe(fs.createWriteStream(`./resources/etfs/${filename}`));
    filenames.filter(f => f.status === "fulfilled")
        .map(f => {
            const filename = (f as PromiseFulfilledResult<string>).value;
            const filePath = `./resources/etfs/${filename}`;
            return fs.createReadStream(filePath)
                .pipe(parse({headers: true}))
                .on('error', error => console.error(error))
                .on('data', row => mainStream.write(row))
                .on('end', () => unlink(filePath));
        })
}

async function collectEtfsByExchange(country: string, exchange: string | null): Promise<string> {
    const csvStream = format({headers: true});
    const filename = composeFilename(exchange ?? country);
    csvStream.pipe(fs.createWriteStream(`./resources/etfs/${filename}`));
    try {
        manualData.forEach(data => csvStream.write(data));
    } catch (e) {
        console.error(e);
    } finally {
        csvStream.end();
    }

    return filename;
}


function composeFilename(subject: string) {
    return `${subject.replaceAll(' ', '-').toLowerCase()}.csv`;
}
