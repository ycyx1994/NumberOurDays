/**
 * Node smoke: adapter + WeappDomain via miniprogram libs (no wx.*).
 */
const path = require('path')
const adapter = require('../miniprogram/domain/adapter')

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
console.log('smoke ok', displays[0].label)
