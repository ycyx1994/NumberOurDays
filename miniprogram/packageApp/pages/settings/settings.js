const store = require('../../storage/events')

Page({
  data: {
    count: 0,
    message: '',
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
  onExport() {
    const text = store.exportJson()
    wx.setClipboardData({
      data: text,
      success: () => this.setData({ message: '已复制 JSON 到剪贴板' }),
    })
  },
  onImportMerge() {
    wx.getClipboardData({
      success: (res) => {
        const r = store.importJson(res.data, false)
        this.setData({ message: r.message, count: store.load().length })
      },
    })
  },
  onImportReplace() {
    wx.showModal({
      title: '覆盖导入',
      content: '将用剪贴板 JSON 覆盖本地纪念日，确定？',
      confirmColor: '#D4553A',
      success: (res) => {
        if (!res.confirm) return
        wx.getClipboardData({
          success: (clip) => {
            const r = store.importJson(clip.data, true)
            this.setData({ message: r.message, count: store.load().length })
          },
        })
      },
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
        this.setData({ message: '已清空', count: 0 })
      },
    })
  },
})
