import {format, parse} from "fast-csv";
import fs from "fs";
import {InstrumentData} from "../common/types/instrument-data";

export async function collectInstruments(instrumentType: string, country: string, exchanges: string[] = [],
                                         extraInstruments: InstrumentData[], instrumentsToModify: { [key: string]: object; }) {
    const collectedInstruments = format({headers: true});
    collectedInstruments.pipe(fs.createWriteStream(composeFilename(`./resources/${instrumentType}s/${country}`)));
    extraInstruments.forEach(data => collectedInstruments.write(data));
    const pathToBaseInstruments = composeFilename(`${__dirname}/assets/${instrumentType}/${country}`);
    return fs.promises.access(pathToBaseInstruments, fs.constants.F_OK)
        .then(() => {
            fs.createReadStream(pathToBaseInstruments)
                .pipe(parse({headers: true}))
                .on('error', error => console.error(error))
                .on('data', (row: InstrumentData) => {
                    if (exchanges.length === 0 || exchanges.some((exchange) => exchange === row.exchange)) {
                        const modifications = instrumentsToModify[`${row.symbol}.${row.exchange}`] ?? {};
                        collectedInstruments.write({...row, ...modifications});
                    }
                })
                .on('end', (rowCount: number) => {
                    console.log(`Pulled from assets ${rowCount} ${instrumentType}`);
                    collectedInstruments.end();
                });
        })
        .catch(err => {
            collectedInstruments.end();
        }).finally(() => {
            console.log(`Added ${extraInstruments.length} extra ${instrumentType}`);
        });
}

function composeFilename(subject: string) {
    return `${subject.replaceAll(' ', '-').toLowerCase()}.csv`;
}
