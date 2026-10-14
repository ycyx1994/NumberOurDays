const store = require('../../storage/events')

Page({
  data: {
    displays: [],
    today: '',
  },
  onShow() {
    this.refresh()
  },
  refresh() {
    const today = require('../../domain/adapter').today()
    this.setData({ today, displays: store.listDisplays() })
  },
  onAdd() {
    wx.navigateTo({ url: '/pages/edit/edit' })
  },
  onOpen(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({ url: `/pages/detail/detail?id=${id}` })
  },
  onSettings() {
    wx.navigateTo({ url: '/pages/settings/settings' })
  },
})
