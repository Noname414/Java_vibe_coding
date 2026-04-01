# Java Vibe Coding 課程教學（W2-W9）

這份 README 是依照目前課程進度與程式碼狀態整理的教學腳本摘要，目標是把第一週的互動小遊戲，逐步推進成可維護的小型遊戲專題骨架。

## 課程目標

- 延續已完成的第一週內容，避免重複講解
- 在 W2-W4 建立遊戲核心觀念：更新循環、碰撞、輸入管理
- 在 W5-W6 進入物件導向拆分，完成 Space Invaders 核心循環
- 在 W7-W8 引導 Tetris 核心邏輯：旋轉、消行、計分
- 在 W9 完成期末專題前置：架構、README、測試案例

## 期末專題對齊重點

- 使用 Swing/AWT
- 鍵盤操作
- 分數系統
- Pause/Resume
- Restart
- Game Over 或 Victory
- Eclipse 可執行
- 避免單一 class 與避免把邏輯全塞在 paintComponent(...)

## 目前程式架構（對應 W5）

- Main: 進入點，啟動視窗
- GameFrame: 遊戲主視窗
- GamePanel: 遊戲更新、輸入、繪圖、碰撞檢查
- GameState: 分數、幀數、暫停、結束狀態
- Player: 玩家移動與邊界限制
- Enemy (abstract): 敵人共同介面
- Meteor / BouncingMeteor: 敵人實作
- Bullet: 子彈物件
- Star: 背景星點

## W2-W9 教學腳本總覽

### W2：讓遊戲有心跳（Game Loop / Timer）

目標：建立 update 與 render 分離概念。

- Demo Prompt 1：改用 javax.swing.Timer，每 16ms 更新一次
- Demo Prompt 2：加入隕石下落並可重生
- Demo Prompt 3：顯示 frame count 或 score

教學點：Timer -> update -> repaint。

Lab Mission：速度可調、P 暫停、每秒加分。

### W3：碰撞偵測與邊界

目標：加入可判斷的規則，讓物件有生命週期。

- Demo Prompt 1：玩家與子彈邊界限制
- Demo Prompt 2：加入左右反彈敵人
- Demo Prompt 3：命中後刪除物件並加分

教學點：先用簡單距離或矩形重疊，不求複雜。

Lab Mission：多隕石、玩家受擊、碰撞箱調整。

### W4：鍵盤輸入管理

目標：從滑鼠追蹤進入可操作遊戲狀態。

- Demo Prompt 1：方向鍵按住移動、放開停止
- Demo Prompt 2：空白鍵射擊，限制最多 3 發
- Demo Prompt 3：P 暫停、R 重來、畫面顯示狀態

教學點：input state 與焦點管理（requestFocusInWindow）。

Lab Mission：平滑移動、射速限制、連發模式。

### W5：Space Invaders（上）物件導向與敵人陣列

目標：從單檔程式升級為可維護多檔案架構。

- Demo Prompt 1：拆分 Main / GameFrame / GamePanel / GameState / Player / Enemy / Bullet
- Demo Prompt 2：ArrayList<Enemy> 建立 3x8 敵人陣列
- Demo Prompt 3：整排左右移動，碰邊下移並反向

教學點：邏輯與繪製分離，不把更新塞進 paintComponent。

Lab Mission：不同敵人分數、清場 Victory、分排速度差。

### W6：Space Invaders（下）命中與回饋

目標：完成可玩的核心循環。

- Demo Prompt 1：子彈命中刪除 + 計分
- Demo Prompt 2：簡易粒子爆炸效果
- Demo Prompt 3：到底 Game Over、清場 Victory

教學點：安全刪除 ArrayList 元素（倒序或 iterator）。

Lab Mission：敵人加速、生命值、敵人子彈。

### W7：Tetris（上）旋轉與矩陣

目標：建立二維陣列與旋轉驗證能力。

- Demo Prompt 1：建立 10x20 棋盤與初始方塊
- Demo Prompt 2：4x4 矩陣旋轉 90 度
- Demo Prompt 3：旋轉非法時取消操作

教學點：AI 可生成轉置邏輯，但要自己驗證正確性。

Lab Mission：新增方塊、左右移動、ghost 預覽。

### W8：Tetris（下）消行與計分

目標：完成 Tetris 核心規則循環。

- Demo Prompt 1：滿行檢查、刪行、上方下移
- Demo Prompt 2：多行消除計分規則
- Demo Prompt 3：出生即碰撞判定 Game Over

教學點：由下往上掃描的重要性。

Lab Mission：P 暫停、R 重來、速度隨分數提升、next piece。

### W9：專題提案與架構搭建

目標：啟動期末專題，建立工程與文件習慣。

- Demo Prompt 1：先定義 class 結構與職責
- Demo Prompt 2：產生 README 初稿（規則/執行/架構/分工）
- Demo Prompt 3：至少 3 個測試案例（步驟/預期/實際）

教學點：專題不只有功能，還要可執行、可說明、可驗證。

Lab Mission：二人分組、建立骨架、交 README 與測試表。

## 建議教學策略

- W5 起強制多檔案，降低期末重構痛苦
- W7-W8 可分流：Tetris 深做或 Space Invaders 強化
- W9 提前收測試模板，避免只交可執行程式

## 如何執行（命令列）

在專案根目錄執行：

```bash
javac *.java
java Main
```

## Eclipse 執行建議

- 匯入為 Java Project
- 確認 JDK 版本可編譯 Swing 程式
- 以 Main.java 作為 Run As > Java Application

## 後續可擴充方向

- Victory 畫面與關卡切換
- 敵人攻擊模式與難度曲線
- 音效與簡單粒子效果
- README 測試案例與里程碑追蹤
