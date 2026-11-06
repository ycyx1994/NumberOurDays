const domain = require('../../domain/adapter')

Page({
  data: { groups: [] },
  onShow() {
    const groups = domain.holidayGroups().map((g) => ({ ...g, expanded: !g.allPast }))
    this.setData({ groups })
  },
  toggle(e) {
    const year = e.currentTarget.dataset.year
    this.setData({
      groups: this.data.groups.map((g) => (g.year === year ? { ...g, expanded: !g.expanded } : g)),
    })
  },
})
