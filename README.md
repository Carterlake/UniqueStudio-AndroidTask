# UniqueStudio-AndroidTask
made in hust, 2018 summer
this is a music player based on Android
目前这个播放器能实现1.检索本地歌曲，播放、暂停、切换音乐，循环/顺序/单曲播放；
                  2.notification显示，控制播放暂停切换（目前发现华为Magic系列不支持此功能可能导致崩溃）
                  3.桌面小部件控制播放暂停切换，需手动添加小部件（目前发现华为Magic系列不支持此功能可能导致崩溃）
                  4.seekbar进度条实时显示并且能拖动改变音乐进度
                  5.歌词显示并且随播放进度滚动，标识当前歌词（歌词识别路径需在代码LrcProgress中手动改写，否则会有bug）
                  6.网易云音乐黑胶唱片的旋转效果
                  7.可以新建，删除歌单，可以歌单内顺序/循环/随机播放
                  8.倒计时停止功能
                  9.搜索歌曲（基于易源（showAPI）提供的QQ音乐API）并下载歌词的功能（下载路径需在代码SearchActivity中手动改写，否则会有bug）


初次练习之作难免有bug，以后有时间会依次更新，现将已发现的bug写下：
1.未在Main2Activity中播放歌曲马上进入play界面会导致崩溃
2.Main2Activity的当前播放时间无法实时更新
3.桌面小部件播放按钮不能实时切换图标
4.由于API接口的问题，搜索界面点击下载音乐会下载一个奇怪的文件（非音乐文件）
5.进入歌词界面时会延迟切换到当前歌词，如果此时暂停歌词将只会标识第一句，继续播放即可正常切换

如有其他bug，欢迎指正
