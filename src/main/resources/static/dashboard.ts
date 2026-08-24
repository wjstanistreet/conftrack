/**
 * ConfTrack dashboard. Plain ES modules, no bundler.
 * Build with: npm run build   (or let IntelliJ's TypeScript service compile on save)
 */

// ---- STATION 08 - BLOCK E - "The page says undefined" -----------------------
// Found with : JavaScript/TypeScript support - effectively WebStorm inside the IDE
//              you already have open. A breakpoint in render() shows the real shape
//              of the object arriving from the API, and Shift+F6 on avgRating renames
//              the field and the one place it is read, together.
// Fixed by   : avgRating -> averageRating, here and where it is read below.
// Run it as  : hand this one to a REMOTE attendee over Code With Me and let them
//              drive on the shared screen. Best twelve minutes you can give the
//              online half of a hybrid room. Pick your driver in advance.
// Rebuild    : npm run build (or let IDEA's TypeScript service compile on save).
//              The compiled dashboard.js is committed so nobody needs Node.
// Notes      : docs/stations/STATION-08.md
interface SessionSummary {
    sessionId: number;
    title: string;
    speaker: string;
    feedbackCount: number;
    averageRating: number;
    score: number;
}

const body = document.querySelector<HTMLTableSectionElement>('#summary-body');
const timing = document.querySelector<HTMLSpanElement>('#timing');
const reload = document.querySelector<HTMLButtonElement>('#reload');

function ratingText(value: number): string {
    return Number.isFinite(value) ? value.toFixed(2) : 'undefined';
}

function render(rows: SessionSummary[]): void {
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
                <td class="num">${ratingText(row.averageRating)}</td>
                <td class="num">${ratingText(row.score)}</td>
            </tr>`)
        .join('');
}

async function load(): Promise<void> {
    const startedAt = performance.now();
    const response = await fetch('/api/dashboard/summary');
    if (!response.ok) {
        if (body !== null) {
            body.innerHTML = `<tr><td colspan="6" class="empty">Request failed: ${response.status}</td></tr>`;
        }
        return;
    }
    const rows = (await response.json()) as SessionSummary[];
    render(rows);
    if (timing !== null) {
        timing.textContent = `${rows.length} sessions in ${Math.round(performance.now() - startedAt)} ms`;
    }
}

reload?.addEventListener('click', () => {
    void load();
});

void load();
