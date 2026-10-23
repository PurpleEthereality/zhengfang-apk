package com.tyust.course.schedule

import android.content.Context
import android.util.AttributeSet
import android.widget.Chronometer
import android.widget.RemoteViews
import android.widget.TextView

/**
 * 桌面组件里的倒计时文字。
 *
 * 框架的 Chronometer 越过 base 之后会继续往负数走，而桌面组件不能保证边界广播一定准时到达
 * （休眠、省电策略、厂商后台限制都可能推迟刷新）。RemoteViews 无法在两次刷新之间重新设置目标，
 * 但可以像普通 Chronometer 一样调用 setBase / setStarted 等标准方法，所以这里继承 Chronometer，
 * 只在文本落到桌面之前把负数改写成 00:00：广播再晚，桌面上也不会出现 -00:01。
 *
 * 布局里通过 [ScheduleWidgetPresentation.ScheduleWidgetRenderer] 的 setChronometer 使用；
 * 宿主（桌面）负责每秒推进，这里的钳制不会额外启动计时器。
 */
@RemoteViews.RemoteView
class ScheduleCountdownText(
    context: Context,
    attrs: AttributeSet? = null
) : Chronometer(context, attrs) {

    override fun setText(text: CharSequence?, type: TextView.BufferType?) {
        // Chronometer 正常显示 "00:42"、"1:02:03"，越过 base 后变成 "-00:01" 这类以符号开头的文本。
        // 只处理这种负数文本，其它文字（包括初始的 "00:00"）原样透传。
        super.setText(if (text.isNullOrEmpty() || text[0].isDigit()) text else ZERO, type)
    }

    private companion object {
        const val ZERO = "00:00"
    }
}
