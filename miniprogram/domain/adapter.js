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
  createId() {
    return WeappDomain.createId()
  },
  validateForm(title, date, unit, interval) {
    return WeappDomain.validateForm(title || '', date || '', unit || 'none', interval || 1)
  },
  sortDisplays(events, today) {
    const json = WeappDomain.sortDisplaysJson(JSON.stringify(events || []), today || WeappDomain.today())
    return parseJson(json, [])
  },
  computeDisplay(event, today) {
    const json = WeappDomain.computeDisplayJson(JSON.stringify(event), today || WeappDomain.today())
    return parseJson(json, null)
  },
  serialize(events) {
    return WeappDomain.serialize(JSON.stringify(events || []))
  },
  parseBackup(text) {
    const obj = parseJson(WeappDomain.parseBackup(text || ''), { events: [], note: null })
    return obj
  },
  merge(local, incoming) {
    const json = WeappDomain.mergeJson(JSON.stringify(local || []), JSON.stringify(incoming || []))
    return parseJson(json, [])
  },
}
