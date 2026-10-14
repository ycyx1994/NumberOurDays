const store = require('../../storage/events')
const domain = require('../../domain/adapter')

Page({
  data: {
    id: '',
    event: null,
    display: null,
    unit: 'DAY',
    heroValue: 0,
    heroText: '',
    elapsedText: '',
  },
  onLoad(query) {
    this.setData({ id: query.id })
  },
  onShow() {
    this.refresh()
  },
  refresh() {
    const event = store.getEvent(this.data.id)
    if (!event) {
      this.setData({ event: null, display: null })
      return
    }
    const display = domain.computeDisplay(event, domain.today())
    this.setData({ event, display }, this.computeUnits)
  },
  computeUnits() {
    const { event, unit } = this.data
    if (!event) return
    const target = domain.targetDuration(event, unit)
    const elapsed = domain.elapsedDuration(event, unit)
    const heroValue = target && target.value != null ? target.value : (this.data.display ? this.data.display.days : 0)
    const heroText = target
      ? [target.unitLabel, target.rest].filter(Boolean).join(' ')
      : '天'
    const elapsedText = elapsed
      ? [`${elapsed.value} ${elapsed.unitLabel}`, elapsed.rest].filter(Boolean).join(' ')
      : ''
    this.setData({ heroValue, heroText, elapsedText })
  },
  setUnit(e) {
    this.setData({ unit: e.currentTarget.dataset.unit }, this.computeUnits)
  },
  onEdit() {
    wx.navigateTo({ url: `/pages/edit/edit?id=${this.data.id}` })
  },
  onDelete() {
    wx.showModal({
      title: '删除',
      content: '删除后无法恢复，确定删除这条纪念日吗？',
      confirmColor: '#D4553A',
      success: (res) => {
        if (!res.confirm) return
        store.remove(this.data.id)
        wx.navigateBack()
      },
    })
  },
})
