export function addMonths(date: string | Date | null, months: number) {
    if (!date) {
        return;
    }
    const res = new Date(date);
    const d = res.getDate();
    res.setMonth(res.getMonth() + +months);
    if (res.getDate() !== d) {
        res.setDate(0);
    }
    return res;
};
