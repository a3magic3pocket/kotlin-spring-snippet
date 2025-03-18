package kotlins.pring.snippet.config

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import jakarta.servlet.http.HttpServletRequest
import kotlins.pring.snippet.config.exception.DuplicateFieldException
import kotlins.pring.snippet.config.exception.NotFoundException
import kotlins.pring.snippet.config.exception.ServiceException
import kotlins.pring.snippet.dto.ErrorRes
import kotlins.pring.snippet.dto.FieldError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.*

@RestControllerAdvice
class ExceptionAdvice {

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseBody
    fun handleHttpMessageNotReadableException(
        httpServletRequest: HttpServletRequest,
        httpMessageNotReadableException: HttpMessageNotReadableException
    ): ResponseEntity<ErrorRes> {
        val unknownFieldName = "unknown"
        val cause = httpMessageNotReadableException.cause
        val fieldErrors = mutableListOf<FieldError>()
        val code = "400"
        val status = HttpStatus.BAD_REQUEST
        var message = "some fields are invalid"
//        println("cause++" + cause)
//        println("Cause class: ${cause?.javaClass?.name}")

        when (cause) {
            is InvalidFormatException -> {
                val fieldName = cause.path.joinToString(" -> ") {
                    if (it.index < 0) {
                        it.fieldName ?: unknownFieldName
                    } else {
                        it.index.toString()
                    }
                }
                val invalidValue = cause.value
                val expectedType = cause.targetType.simpleName

                fieldErrors.add(
                    FieldError(
                        field = fieldName,
                        message = "The value of '$fieldName' is invalid: '$invalidValue' (expected: $expectedType)"
                    )
                )
            }

            is MismatchedInputException -> {
                message = "some fields are missing"
                val fieldName = cause.path.joinToString(" -> ") {
                    if (it.index < 0) {
                        it.fieldName ?: unknownFieldName
                    } else {
                        it.index.toString()
                    }
                }
                fieldErrors.add(
                    FieldError(
                        field = fieldName,
                        message = "$fieldName is required"
                    )
                )
            }

            else -> {
                println("다른 예외 발생")
            }
        }

        val errorRes = ErrorRes(
            code = code,
            timestamp = Date(),
            path = httpServletRequest.requestURI,
            message = message,
            fieldErrors = fieldErrors
        )

        return ResponseEntity(errorRes, status)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseBody
    fun handleMethodArgumentNotValidException(
        httpServletRequest: HttpServletRequest,
        methodArgumentNotValidException: MethodArgumentNotValidException,
    ): ErrorRes {
        val code = "400"
        val message = "some fields are invalid"
        val fieldErrors = methodArgumentNotValidException.bindingResult.fieldErrors.map { fieldError ->
            FieldError(
                field = fieldError.field,
                message = fieldError.defaultMessage ?: "Invalid value"
            )
        }

        return ErrorRes(
            code = code,
            timestamp = Date(),
            path = httpServletRequest.requestURI,
            message = message,
            fieldErrors = fieldErrors
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateFieldException::class)
    @ResponseBody
    fun handleDuplicateFieldException(
        httpServletRequest: HttpServletRequest,
        duplicateFieldException: DuplicateFieldException,
    ): ErrorRes {
        return ErrorRes(
            code = duplicateFieldException.code,
            timestamp = Date(),
            path = httpServletRequest.requestURI,
            message = "there are duplicate fields",
            fieldErrors = listOf(
                FieldError(
                    field = duplicateFieldException.field,
                    message = duplicateFieldException.message ?: "${duplicateFieldException.field} is duplicate"
                )
            )
        )
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException::class)
    @ResponseBody
    fun handleNotFoundException(
        httpServletRequest: HttpServletRequest,
        notFoundException: NotFoundException,
    ): ErrorRes {
        return ErrorRes(
            code = notFoundException.code,
            timestamp = Date(),
            path = httpServletRequest.requestURI,
            message = "not found",
            fieldErrors = listOf()
        )
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServiceException::class)
    @ResponseBody
    fun handleServiceException(
        httpServletRequest: HttpServletRequest,
        serviceException: ServiceException,
    ): ErrorRes {
        return ErrorRes(
            code = serviceException.code,
            timestamp = Date(),
            path = httpServletRequest.requestURI,
            message = serviceException.message ?: "internal server error",
            fieldErrors = listOf()
        )
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    @ResponseBody
    fun handleException(
        httpServletRequest: HttpServletRequest,
        exception: Exception,
    ): ErrorRes {
        exception.printStackTrace()

        return ErrorRes(
            code = "500",
            timestamp = Date(),
            path = httpServletRequest.requestURI,
            message = "internal server error",
            fieldErrors = listOf()
        )
    }
}