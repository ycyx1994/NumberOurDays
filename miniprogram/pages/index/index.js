const store = require('../../storage/events')
const domain = require('../../domain/adapter')

Page({
  data: {
    displays: [],
    hero: null,
    today: '',
  },
  onShow() {
    this.refresh()
  },
  refresh() {
    const today = domain.today()
    const displays = store.listDisplays()
    const hero = domain.pickNearest(displays) || displays[0] || null
    this.setData({ today, displays, hero })
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
