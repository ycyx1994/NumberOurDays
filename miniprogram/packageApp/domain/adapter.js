/**
 * Thin adapter over KMP JS WeappDomain.
 * WeChat host should only depend on this module.
 */
const shared = require('../libs/shared/NumberOurDays-shared.js')
const WeappDomain = shared.com.numberourdays.app.weapp.WeappDomain

function parseJson(text, fallback) {
  try {
    return JSON.parse(text)
  } catch (e) {
    return fallback
  }
}

module.exports = {
  today() {
    return WeappDomain.today()
  },
  isValidDate(text) {
    return WeappDomain.isValidDate(text)
  },
  parseDate(text) {
    return WeappDomain.parseDate(text || '')
  },
  formatDate(iso) {
    return WeappDomain.formatDate(iso || '')
  },
  createId() {
    return WeappDomain.createId()
  },
  validateForm(title, date, unit, interval) {
    return WeappDomain.validateForm(title || '', date || '', unit || 'none', interval == null ? 1 : interval)
  },
  sortDisplays(events, today) {
    const json = WeappDomain.sortDisplaysJson(JSON.stringify(events || []), today || WeappDomain.today())
    return parseJson(json, [])
  },
  pickNearest(displays) {
    return parseJson(WeappDomain.pickNearestJson(JSON.stringify(displays || [])), null)
  },
  computeDisplay(event, today) {
    const json = WeappDomain.computeDisplayJson(JSON.stringify(event), today || WeappDomain.today())
    return parseJson(json, null)
  },
  targetDuration(event, unit, today) {
    return parseJson(WeappDomain.targetDurationJson(JSON.stringify(event), unit || 'DAY', today || WeappDomain.today()), null)
  },
  elapsedDuration(event, unit, today) {
    return parseJson(WeappDomain.elapsedDurationJson(JSON.stringify(event), unit || 'DAY', today || WeappDomain.today()), null)
  },
  serialize(events) {
    return WeappDomain.serialize(JSON.stringify(events || []))
  },
  parseBackup(text) {
    return parseJson(WeappDomain.parseBackup(text || ''), { events: [], note: null })
  },
  holidayCountdown(today) {
    return parseJson(WeappDomain.holidayCountdownJson(today || WeappDomain.today()), null)
  },
  holidayList(today) {
    return parseJson(WeappDomain.holidayListJson(today || WeappDomain.today()), [])
  },
  merge(local, incoming) {
    const json = WeappDomain.mergeJson(JSON.stringify(local || []), JSON.stringify(incoming || []))
    return parseJson(json, [])
  },
}
