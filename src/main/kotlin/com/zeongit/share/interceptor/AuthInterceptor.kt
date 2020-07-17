package com.zeongit.share.interceptor

import com.zeongit.share.annotations.Auth
import com.zeongit.share.component.UserComponent
import com.zeongit.share.constant.BaseConstant
import com.zeongit.share.exception.NotFoundException
import com.zeongit.share.exception.PermissionException
import com.zeongit.share.exception.SignInException
import com.zeongit.share.util.CookieUtil
import com.zeongit.share.util.DateUtil
import com.zeongit.share.util.JwtUtil
import org.springframework.lang.Nullable
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author fjj
 * 登录验证拦截器
 */
class AuthInterceptor(private val userComponent: UserComponent
) : HandlerInterceptor {
    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod) {
            val handlerMethod = handler as HandlerMethod?
            //程序运行的bean
            handlerMethod!!.beanType
            //运行的方法
            val method = handlerMethod.method
            val auth = method.getAnnotation(Auth::class.java)
            try {
                val cookieMap = CookieUtil.readCookieMap(request)
                val tokenCookie = cookieMap["token"]
                val token = if (tokenCookie != null) {
                    tokenCookie.value
                } else {
                    request.getHeader("token")
                }
                val claims = JwtUtil.parseJWT(token, BaseConstant.JWT_SECRET)
                val id = (claims["id"] as String).toInt()
                //过期时间
                val exp = Date(claims["exp"]!!.toString().toLong() * 1000)
                //生成时间
                val nbf = Date(claims["nbf"]!!.toString().toLong() * 1000)

                val user = userComponent.get(id)

                if (DateUtil.getDistanceTimestamp(Date(), exp) < 0) {
                    throw SignInException("用户登录已过期")
                }
                if (DateUtil.getDistanceTimestamp(user.lastModifiedDate!!, nbf) < 0) {
                    throw SignInException("请重新登录")
                }

                request.setAttribute("user_id", id)

                try {
                    request.setAttribute("user_info_id", userComponent.getByUserId(id).id!!)
                } catch (e: NotFoundException) {
                    if (auth != null && !auth.middleware) {
                        throw PermissionException("请完善你的信息", 503)
                    }
                }
            } catch (e: Exception) {
                if (method.isAnnotationPresent(Auth::class.java)) {
                    throw if (e is SignInException || e is PermissionException) {
                        e
                    } else SignInException("请重新登录")
                }
            }
        }
        return true
    }

    @Throws(Exception::class)
    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, @Nullable modelAndView: ModelAndView?) {
    }

    @Throws(Exception::class)
    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, @Nullable ex: java.lang.Exception?) {
    }
}
