package kotlins.pring.snippet.controller

import jakarta.validation.Valid
import kotlins.pring.snippet.dto.*
import kotlins.pring.snippet.entity.Fruit
import kotlins.pring.snippet.repository.FruitQuerydslRepository
import kotlins.pring.snippet.service.FruitService
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/v1/fruits")
class FruitController(
    private val fruitService: FruitService,
    private val fruitQuerydslRepository: FruitQuerydslRepository
) {
    companion object {
        const val FRUIT_ID = "fruit_id"
        const val FRUIT_NAME = "fruit_name"
        const val FRUIT_ORIGIN = "fruit_origin"
        const val FRUIT_FILE = "file"
    }

    @GetMapping(value = ["/{$FRUIT_ID:[0-9]+}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun retrieveFruit(
        @PathVariable(name = FRUIT_ID) fruitId: Long,
    ): ResponseEntity<DataRes<FruitResDto>> {
        val savedFruit = fruitService.retrieveFruit(
            fruitId = fruitId
        )

        return ResponseEntity.ok().body(
            DataRes(
                FruitResDto(
                    id = savedFruit.id!!,
                    name = savedFruit.name,
                    origin = savedFruit.origin
                )
            )
        )
    }

    @GetMapping(value = ["/search"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun listFruitByName(
        @RequestParam(name = FRUIT_NAME) name: String,
    ): ResponseEntity<DataRes<List<FruitResDto>>> {
        val savedFruits = fruitService.listFruitsByName(
            name = name
        )

        return ResponseEntity.ok().body(
            DataRes(
                savedFruits.map {
                    FruitResDto(
                        id = it.id!!,
                        name = it.name,
                        origin = it.origin
                    )
                }
            )
        )
    }

    @PostMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createFruit(
        @RequestBody fruitReqDto: FruitReqDto
    ): ResponseEntity<DataRes<FruitResDto>> {
        val savedFruit = fruitService.createFruit(
            fruitReqDto = fruitReqDto
        )

        return ResponseEntity.ok().body(
            DataRes(
                FruitResDto(
                    id = savedFruit.id!!,
                    name = savedFruit.name,
                    origin = savedFruit.origin
                )
            )
        )
    }

    @PostMapping(value = ["/form-data"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createFruitByFormData(
        @ModelAttribute fruitReqFormDataDto: FruitReqFormDataDto
    ): ResponseEntity<DataRes<FruitResDto>> {
        val savedFruit = fruitService.createFruitByFormData(
            fruitReqFormDataDto = fruitReqFormDataDto
        )

        return ResponseEntity.ok().body(
            DataRes(
                FruitResDto(
                    id = savedFruit.id!!,
                    name = savedFruit.name,
                    origin = savedFruit.origin
                )
            )
        )
    }

    @PostMapping(value = ["/form-data-request-param"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createFruitByFormDataRequestParam(
        @RequestParam(name = FRUIT_NAME) name: String,
        @RequestParam(name = FRUIT_ORIGIN) origin: String,
    ): ResponseEntity<DataRes<FruitResDto>> {
        val savedFruit = fruitService.createFruitByFormData(
            fruitReqFormDataDto = FruitReqFormDataDto(
                name = name,
                origin = origin
            )
        )

        return ResponseEntity.ok().body(
            DataRes(
                FruitResDto(
                    id = savedFruit.id!!,
                    name = savedFruit.name,
                    origin = savedFruit.origin
                )
            )
        )
    }

    @PostMapping(value = ["/file"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createFruitWithFile(
        @RequestPart(FRUIT_FILE) file: MultipartFile,
        @ModelAttribute fruitFileDto: FruitFileDto,
    ): ResponseEntity<DataRes<FruitResDto>> {
        val savedFruit = fruitService.createFruitWithFile(
            file = file,
            fruitFileDto = fruitFileDto
        )

        return ResponseEntity.ok().body(
            DataRes(
                FruitResDto(
                    id = savedFruit.id!!,
                    name = savedFruit.name,
                    origin = savedFruit.origin
                )
            )
        )
    }

    @PostMapping(value = ["/size"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createFruitWithSize(
        @RequestBody @Valid fruitSizeDto: FruitSizeDto,
    ): ResponseEntity<DataRes<FruitResDto>> {
        val savedFruit = fruitService.createFruitWithSize(
            fruitSizeDto = fruitSizeDto
        )

        return ResponseEntity.ok().body(
            DataRes(
                FruitResDto(
                    id = savedFruit.id!!,
                    name = savedFruit.name,
                    origin = savedFruit.origin
                )
            )
        )
    }

    @GetMapping("")
    fun listFruits(
    ) {

    }

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun listFruitsPageableByOrigin(
        @RequestParam(name = FRUIT_ORIGIN) origin: String,
        pageable: Pageable,
    ): ResponseEntity<DataPageableRes<Fruit>> {
        val fruitPage = fruitService.listFruitsPageable(
            origin = origin,
            pageable = pageable
        )

        return ResponseEntity.ok().body(
            DataPageableRes(
                totalCount = fruitPage.totalElements,
                data = fruitPage.content,
                page = PageInfo(
                    totalPages = fruitPage.totalPages,
                    number = fruitPage.number,
                    size = fruitPage.size
                )
            )
        )
    }

    @PutMapping(value = ["/{$FRUIT_ID:[0-9]+}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateFruit() {

    }

    @DeleteMapping(value = ["/{$FRUIT_ID:[0-9]+}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteFruit() {

    }

    @GetMapping(value = ["/expt"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun expt(
    ): String {
        // ex1
//        fruitQuerydslRepository.getFruitsWithQuantity()

        // ex2
//        val pageable = PageRequest.of(2, 2)
//        val result = fruitQuerydslRepository.getFruitsWithQuantityPageable(
//            pageable = pageable
//        )
//        println("result++" + result)
//        println("result++" + result.size)
//        println("result++" + result.content)

        // ex3
//        fruitQuerydslRepository.getLatestAndPreviousOrderDiff()

        // ex4
//        val result = fruitQuerydslRepository.getFruitsOrderedByQuantity(
//            quantity = 2
//        )

        // ex5
//        val result = fruitQuerydslRepository.getFruitsWithOrderSummary()
//        println("result++" + result)

        // ex6
        val result = fruitQuerydslRepository.joinByFruitName(
            name = "바나나"
        )
        println("result++" + result)

        return "Hello"
    }

}