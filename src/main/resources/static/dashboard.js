"use strict";
/**
 * ConfTrack dashboard. Plain ES modules, no bundler.
 * Build with: npm run build   (or let IntelliJ's TypeScript service compile on save)
 */
const body = document.querySelector('#summary-body');
const timing = document.querySelector('#timing');
const reload = document.querySelector('#reload');
function ratingText(value) {
    return Number.isFinite(value) ? value.toFixed(2) : 'undefined';
}
function render(rows) {
    if (body === null) {
        return;
    }
    if (rows.length === 0) {
        body.innerHTML = '<tr><td colspan="6" class="empty">No sessions yet.</td></tr>';
        return;
    }
    body.innerHTML = rows
        .map((row) => `
            <tr>
                <td>${row.sessionId}</td>
                <td>${row.title}</td>
                <td>${row.speaker}</td>
                <td class="num">${row.feedbackCount}</td>
                <td class="num">${ratingText(row.avgRating)}</td>
                <td class="num">${ratingText(row.score)}</td>
            </tr>`)
        .join('');
}
async function load() {
    const startedAt = performance.now();
    const response = await fetch('/api/dashboard/summary');
    if (!response.ok) {
        if (body !== null) {
            body.innerHTML = `<tr><td colspan="6" class="empty">Request failed: ${response.status}</td></tr>`;
        }
        return;
    }
    const rows = (await response.json());
    render(rows);
    if (timing !== null) {
        timing.textContent = `${rows.length} sessions in ${Math.round(performance.now() - startedAt)} ms`;
    }
}
reload?.addEventListener('click', () => {
    void load();
});
void load();
//# sourceMappingURL=dashboard.js.map