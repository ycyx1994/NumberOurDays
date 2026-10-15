const store = require('../../storage/events')
const domain = require('../../domain/adapter')

Page({
  data: {
    displays: [],
    slides: [],
    current: 0,
    unit: 'DAY',
    heroValue: 0,
    heroSuffix: '天',
    today: '',
  },
  onShow() {
    this.refresh()
  },
  refresh() {
    const today = domain.today()
    const displays = store.listDisplays()
    // carousel order: pickNearest first, then the rest (Android carouselList)
    const nearest = domain.pickNearest(displays)
    const slides = nearest
      ? [nearest].concat(displays.filter((d) => d.id !== nearest.id))
      : displays
    this.setData({ today, displays, slides, current: 0 }, () => this.applyUnit())
  },
  applyUnit() {
    const { slides, current, unit } = this.data
    const slide = slides[current]
    if (!slide) {
      this.setData({ heroValue: 0, heroSuffix: '天' })
      return
    }
    const event = store.getEvent(slide.id)
    if (!event) {
      this.setData({ heroValue: slide.days, heroSuffix: '天' })
      return
    }
    const dur = domain.targetDuration(event, unit)
    if (!dur) {
      this.setData({ heroValue: slide.days, heroSuffix: '天' })
      return
    }
    const suffix = [dur.unitLabel, dur.rest].filter(Boolean).join(' ')
    this.setData({ heroValue: dur.value, heroSuffix: suffix || '天' })
  },
  onSwiperChange(e) {
    this.setData({ current: e.detail.current }, this.applyUnit)
  },
  setUnit(e) {
    this.setData({ unit: e.currentTarget.dataset.unit }, this.applyUnit)
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
