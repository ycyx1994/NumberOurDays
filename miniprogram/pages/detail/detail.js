const store = require('../../storage/events')
const domain = require('../../domain/adapter')

Page({
  data: {
    id: '',
    event: null,
    display: null,
    unit: 'DAY',
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
    this.setData({ event, display }, this.computeElapsed)
  },
  computeElapsed() {
    const { event, unit } = this.data
    if (!event) return
    // reuse serialize→display path for elapsed: we already have display.days for countdown
    // For elapsed text, parse backup isn't needed — use label / elapsedDays from display for DAY
    const display = this.data.display
    if (!display) return
    if (unit === 'DAY') {
      this.setData({ elapsedText: `${display.elapsedDays} 天` })
    } else if (unit === 'MONTH') {
      // approximate from elapsedDays for MVP display string; full breakdown is in native apps
      const months = Math.floor(display.elapsedDays / 30)
      const days = display.elapsedDays % 30
      this.setData({ elapsedText: `${months} 月 ${days} 天` })
    } else {
      const years = Math.floor(display.elapsedDays / 365)
      const rest = display.elapsedDays % 365
      const months = Math.floor(rest / 30)
      const days = rest % 30
      this.setData({ elapsedText: `${years} 年 ${months} 月 ${days} 天` })
    }
  },
  setUnit(e) {
    this.setData({ unit: e.currentTarget.dataset.unit }, this.computeElapsed)
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
