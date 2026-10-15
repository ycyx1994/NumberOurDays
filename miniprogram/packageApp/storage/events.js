const domain = require('../domain/adapter')
const KEY = 'nod:events'

function load() {
  try {
    const raw = wx.getStorageSync(KEY)
    if (!raw) return []
    if (Array.isArray(raw)) return raw
    return JSON.parse(raw) || []
  } catch (e) {
    return []
  }
}

function save(events) {
  wx.setStorageSync(KEY, JSON.stringify(events || []))
}

function listDisplays() {
  return domain.sortDisplays(load(), domain.today())
}

function getEvent(id) {
  return load().find((e) => e.id === id) || null
}

function upsert(event) {
  const events = load()
  const i = events.findIndex((e) => e.id === event.id)
  if (i >= 0) events[i] = event
  else events.push(event)
  save(events)
  return events
}

function remove(id) {
  save(load().filter((e) => e.id !== id))
}

function createId() {
  return domain.createId()
}

function exportJson() {
  return domain.serialize(load())
}

function importJson(text, replace) {
  const { events: incoming, note } = domain.parseBackup(text)
  if (!incoming.length) {
    return { ok: false, message: note || '备份中没有有效纪念日' }
  }
  if (replace) {
    save(incoming)
    return { ok: true, message: note || '已覆盖导入', count: incoming.length }
  }
  const merged = domain.merge(load(), incoming)
  save(merged)
  return { ok: true, message: note || '已合并导入', count: merged.length }
}

function clearAll() {
  save([])
}

module.exports = {
  load,
  save,
  listDisplays,
  getEvent,
  upsert,
  remove,
  createId,
  exportJson,
  importJson,
  clearAll,
}
