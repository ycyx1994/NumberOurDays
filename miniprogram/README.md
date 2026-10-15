# NumberOurDays 微信小程序

AppID 与旧 Taro 项目一致：`wxea507de2dccbf14a`。

## 构建

```bash
./scripts/build-weapp-shared.sh   # 编译 :shared → miniprogram/libs/shared
# 若 Node/微信报缺 @js-joda：
mkdir -p miniprogram/libs/shared/node_modules
npm install --prefix miniprogram/libs/shared @js-joda/core@3.2.0 format-util@1.0.5
```

微信开发者工具打开仓库根目录（`project.config.json` 已指向 `miniprogram/`）。

## 结构

- `domain/adapter.js` — KMP JS `WeappDomain` 封装
- `storage/events.js` — `wx.setStorage` + JSON 备份 v1
- `pages/*` — 首页 / 详情 / 编辑 / 设置

领域规则勿在本目录重写。


## 分包

- 主包：`app.*` + `pages/entry`（极薄启动）
- `packageApp/`：首页/详情/编辑/设置 + `domain` / `storage` / `libs`（Kotlin/JS）

主包远小于 2MB；整包受微信分包总限额约束（约 20–30MB 量级），便于后续加图或假日页。
