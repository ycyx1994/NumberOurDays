const store = require('../../storage/events')
const domain = require('../../domain/adapter')

const UNITS = [
  { v: 'NONE', label: '不重复' },
  { v: 'DAY', label: '天' },
  { v: 'WEEK', label: '周' },
  { v: 'MONTH', label: '月' },
  { v: 'YEAR', label: '年' },
]

Page({
  data: {
    id: '',
    title: '',
    date: '',
    note: '',
    repeatOn: false,
    unit: 'DAY',
    interval: 1,
    units: UNITS.map((u) => u.label),
    unitIndex: 1,
    error: '',
  },
  onLoad(query) {
    const today = domain.today()
    if (query.id) {
      const e = store.getEvent(query.id)
      if (e) {
        const unitIndex = Math.max(0, UNITS.findIndex((u) => u.v === (e.repeatUnit || 'NONE').toUpperCase()))
        this.setData({
          id: e.id,
          title: e.title,
          date: e.date,
          note: e.note || '',
          repeatOn: (e.repeatUnit || 'NONE') !== 'NONE',
          unit: (e.repeatUnit || 'NONE').toUpperCase(),
          interval: e.repeatInterval || 1,
          unitIndex,
        })
        return
      }
    }
    this.setData({ date: today, unit: 'DAY', unitIndex: 1 })
  },
  onTitle(e) {
    this.setData({ title: e.detail.value })
  },
  onDate(e) {
    this.setData({ date: e.detail.value })
  },
  onNote(e) {
    this.setData({ note: e.detail.value })
  },
  onRepeat(e) {
    this.setData({ repeatOn: e.detail.value, unit: e.detail.value ? 'DAY' : 'NONE' })
    if (e.detail.value) this.setData({ unitIndex: 1 })
    else this.setData({ unitIndex: 0 })
  },
  onUnit(e) {
    const idx = Number(e.detail.value)
    this.setData({ unitIndex: idx, unit: UNITS[idx].v })
  },
  onInterval(e) {
    this.setData({ interval: Number(e.detail.value) || 1 })
  },
  onSave() {
    const { title, date, unit, interval, note, id, repeatOn } = this.data
    const err = domain.validateForm(title, date, repeatOn ? unit : 'NONE', interval)
    if (err) {
      this.setData({ error: err })
      return
    }
    const now = Date.now()
    const existing = id ? store.getEvent(id) : null
    const event = {
      id: (existing && existing.id) || store.createId(),
      title: title.trim(),
      date,
      note: note && note.trim() ? note.trim() : null,
      starred: (existing && existing.starred) || false,
      starRank: (existing && existing.starRank) || null,
      repeatUnit: (repeatOn ? unit : 'NONE').toLowerCase(),
      repeatInterval: interval,
      remindEnabled: (existing && existing.remindEnabled) || false,
      remindHour: (existing && existing.remindHour) || 8,
      remindMinute: (existing && existing.remindMinute) || 0,
      createdAt: (existing && existing.createdAt) || now,
      updatedAt: now,
    }
    store.upsert(event)
    wx.navigateBack()
  },
})
