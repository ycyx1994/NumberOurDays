const store = require('../../storage/events')

Page({
  data: {
    count: 0,
    message: '',
    pasteText: '',
    unitIndex: 0,
    unitLabels: ['天', '月', '年'],
    unitValues: ['DAY', 'MONTH', 'YEAR'],
  },
  onShow() {
    const u = wx.getStorageSync('nod:home_unit') || 'DAY'
    const idx = Math.max(0, this.data.unitValues.indexOf(u))
    this.setData({ count: store.load().length, unitIndex: idx })
  },
  onHomeUnit(e) {
    const idx = Number(e.detail.value)
    const v = this.data.unitValues[idx]
    wx.setStorageSync('nod:home_unit', v)
    this.setData({ unitIndex: idx, message: '首页大卡单位：' + this.data.unitLabels[idx] })
  },
  onPasteInput(e) {
    this.setData({ pasteText: e.detail.value })
  },
  onExport() {
    const text = store.exportJson()
    wx.setClipboardData({
      data: text,
      success: () => this.setData({ message: '已复制 JSON 到剪贴板', pasteText: text }),
      fail: () => this.setData({ message: '复制失败，JSON 已填入下方输入框', pasteText: text }),
    })
  },
  applyImport(text, replace) {
    const raw = (text || '').trim()
    if (!raw) {
      this.setData({ message: '请先粘贴备份 JSON' })
      return
    }
    const r = store.importJson(raw, replace)
    this.setData({
      message: r.message || (replace ? '已覆盖导入' : '已合并导入'),
      count: store.load().length,
    })
    if (r.ok) this.setData({ pasteText: '' })
  },
  onImportMerge() {
    // Prefer typed/pasted box; fall back to clipboard
    if (this.data.pasteText && this.data.pasteText.trim()) {
      this.applyImport(this.data.pasteText, false)
      return
    }
    wx.getClipboardData({
      success: (res) => {
        if (!res.data || !String(res.data).trim()) {
          this.setData({ message: '剪贴板为空，请在下方粘贴 JSON 后导入' })
          return
        }
        this.setData({ pasteText: res.data })
        this.applyImport(res.data, false)
      },
      fail: () => {
        this.setData({ message: '无法读剪贴板，请长按输入框粘贴 JSON' })
      },
    })
  },
  onImportReplace() {
    const doReplace = () => this.applyImport(this.data.pasteText, true)
    if (!this.data.pasteText || !this.data.pasteText.trim()) {
      wx.getClipboardData({
        success: (res) => {
          this.setData({ pasteText: res.data || '' })
          if (!String(res.data || '').trim()) {
            this.setData({ message: '剪贴板为空，请在下方粘贴 JSON 后覆盖导入' })
            return
          }
          wx.showModal({
            title: '覆盖导入',
            content: '将用剪贴板 JSON 覆盖本地纪念日，确定？',
            confirmColor: '#D4553A',
            success: (r) => { if (r.confirm) doReplace() },
          })
        },
        fail: () => this.setData({ message: '无法读剪贴板，请长按输入框粘贴 JSON' }),
      })
      return
    }
    wx.showModal({
      title: '覆盖导入',
      content: '将用输入框中的 JSON 覆盖本地纪念日，确定？',
      confirmColor: '#D4553A',
      success: (r) => { if (r.confirm) doReplace() },
    })
  },
  onClear() {
    wx.showModal({
      title: '清空',
      content: '确定清空全部纪念日？',
      confirmColor: '#D4553A',
      success: (res) => {
        if (!res.confirm) return
        store.clearAll()
        this.setData({ message: '已清空', count: 0, pasteText: '' })
      },
    })
  },
})
