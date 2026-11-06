/**
 * Node smoke: adapter + WeappDomain via miniprogram libs (no wx.*).
 */
const path = require('path')
const adapter = require('../miniprogram/packageApp/domain/adapter')

const today = adapter.today()
console.log('today', today)

const ev = {
  id: 'e1',
  title: '考试',
  date: '2026-10-20',
  note: null,
  starred: true,
  starRank: 1,
  repeatUnit: 'none',
  repeatInterval: 1,
  remindEnabled: false,
  remindHour: 8,
  remindMinute: 0,
  createdAt: 1,
  updatedAt: 1,
}
const displays = adapter.sortDisplays([ev], '2026-10-14')
if (!Array.isArray(displays) || displays.length !== 1) {
  console.error('FAIL sortDisplays', displays)
  process.exit(1)
}
if (displays[0].days !== 6) {
  console.error('FAIL days expected 6 got', displays[0].days)
  process.exit(1)
}
const backup = adapter.serialize([ev])
const parsed = adapter.parseBackup(backup)
if (!parsed.events || parsed.events.length !== 1 || parsed.events[0].id !== 'e1') {
  console.error('FAIL parseBackup', parsed)
  process.exit(1)
}
const err = adapter.validateForm('', '2026-10-20', 'none', 1)
if (err !== '请填写标题') {
  console.error('FAIL validateForm', err)
  process.exit(1)
}
const bad = { ...ev, id: 'bad', title: 'ok\ttab', date: '2026-10-21' }
const mixed = adapter.sortDisplays([ev, bad], '2026-10-14')
if (!Array.isArray(mixed) || mixed.length !== 2) {
  console.error('FAIL escape title', mixed)
  process.exit(1)
}
const dur = adapter.targetDuration(ev, 'MONTH', '2026-10-14')
if (!dur || typeof dur.value !== 'number' || !dur.unitLabel) {
  console.error('FAIL duration', dur)
  process.exit(1)
}
const holiday = adapter.holidayCountdown('2026-10-08')
if (!holiday || holiday.name !== '元旦' || holiday.start !== '2027-01-01') {
  console.error('FAIL holidayCountdown expected 2027 元旦 got', holiday)
  process.exit(1)
}
const holidayRows = adapter.holidayList('2026-10-08')
if (!Array.isArray(holidayRows) || holidayRows.length !== 14) {
  console.error('FAIL holidayList expected 14 rows got', holidayRows && holidayRows.length)
  process.exit(1)
}
const groups = adapter.holidayGroups('2026-11-06')
if (!Array.isArray(groups) || groups.length !== 2) {
  console.error('FAIL holidayGroups expected 2 groups got', groups)
  process.exit(1)
}
if (groups[0].year !== 2027 || groups[0].allPast !== false || groups[0].rows.length !== 7) {
  console.error('FAIL holidayGroups[0] expected 2027 live 7 rows got', groups[0] && { y: groups[0].year, p: groups[0].allPast, n: groups[0].rows && groups[0].rows.length })
  process.exit(1)
}
if (groups[1].year !== 2026 || groups[1].allPast !== true) {
  console.error('FAIL holidayGroups[1] expected 2026 allPast got', groups[1] && { y: groups[1].year, p: groups[1].allPast })
  process.exit(1)
}
console.log('smoke ok', displays[0].label, 'dur', dur)

