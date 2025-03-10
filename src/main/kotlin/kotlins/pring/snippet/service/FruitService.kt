package kotlins.pring.snippet.service

import kotlins.pring.snippet.dto.*
import kotlins.pring.snippet.entity.Fruit
import kotlins.pring.snippet.repository.FruitRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Service
class FruitService(
    private val fruitRepository: FruitRepository
) {
    @Transactional(rollbackFor = [Exception::class])
    fun createFruit(fruitReqDto: FruitReqDto): Fruit {
        val fruit = Fruit(
            name = fruitReqDto.name,
            origin = fruitReqDto.origin,
        )

        return fruitRepository.save(fruit)
    }

    @Transactional(rollbackFor = [Exception::class])
    fun createFruitByFormData(fruitReqFormDataDto: FruitReqFormDataDto): Fruit {
        val fruit = Fruit(
            name = fruitReqFormDataDto.name,
            origin = fruitReqFormDataDto.origin,
        )

        return fruitRepository.save(fruit)
    }

    @Transactional(rollbackFor = [Exception::class])
    fun createFruitWithFile(file: MultipartFile, fruitFileDto: FruitFileDto): Fruit {
        val fruit = Fruit(
            name = fruitFileDto.fruitName,
            origin = fruitFileDto.fruitOrigin,
        )

        return fruitRepository.save(fruit)
    }

    @Transactional(rollbackFor = [Exception::class])
    fun createFruitWithSize(fruitSizeDto: FruitSizeDto): Fruit {
        val fruit = Fruit(
            name = fruitSizeDto.name,
            origin = fruitSizeDto.origin,
            size = fruitSizeDto.size
        )

        return fruitRepository.save(fruit)
    }

    @Transactional(rollbackFor = [Exception::class])
    fun createFruits(fruitReqDtos: List<FruitReqDto>): MutableList<Fruit> {
        val fruits = fruitReqDtos.map {
            Fruit(
                name = it.name,
                origin = it.origin,
            )
        }

        return fruitRepository.saveAll(fruits)
    }

    @Transactional(rollbackFor = [Exception::class])
    fun updateFruit(fruitUpdateDto: UpdateDto<FruitReqDto>): Fruit {
        val savedFruit = fruitRepository.findByIdOrNull(id = fruitUpdateDto.id) ?: throw Exception("not found")

        savedFruit.origin = fruitUpdateDto.data.origin
        savedFruit.name = fruitUpdateDto.data.name

        return savedFruit
    }

    @Transactional(rollbackFor = [Exception::class])
    fun updateFruits(fruitUpdateDtos: List<UpdateDto<FruitReqDto>>): List<Fruit> {
        val fruitReqDtoMap = fruitUpdateDtos.associate { it.id to it.data }
        val savedFruits = fruitRepository.findByIdIn(ids = fruitReqDtoMap.keys.toList())
        for (savedFruit in savedFruits) {
            val fruitReqDto = fruitReqDtoMap[savedFruit.id] ?: throw Exception("not found fruit ${savedFruit.id}")

            savedFruit.origin = fruitReqDto.origin
            savedFruit.name = fruitReqDto.name
            savedFruit.updatedAt = ZonedDateTime.now()
        }

        return savedFruits
    }

    @Transactional(readOnly = true)
    fun listFruit(): List<Fruit> {
        val fruits = fruitRepository.findAll()

        return fruits.toList()
    }

    @Transactional(readOnly = true)
    fun retrieveFruit(fruitId: Long): Fruit {
        val fruit = fruitRepository.findByIdOrNull(id = fruitId) ?: throw Exception("not found")

        return fruit
    }

    @Transactional(readOnly = true)
    fun listFruitsByName(name: String): List<Fruit> {
        val fruits = fruitRepository.findByName(name = name)
        if (fruits.isEmpty()) {
            throw Exception("not found")
        }

        return fruits
    }

    @Transactional(readOnly = true)
    fun listFruitsInOrigin(origins: List<String>): List<Fruit> {
        val fruits = fruitRepository.findByOriginIn(origins = origins)
        if (fruits.isEmpty()) {
            throw Exception("not found")
        }

        return fruits
    }

    @Transactional(readOnly = true)
    fun listFruitsLikeName(name: String): List<Fruit> {
        val fruits = fruitRepository.findByNameContaining(name = name)
        if (fruits.isEmpty()) {
            throw Exception("not found")
        }

        return fruits
    }

    @Transactional(readOnly = true)
    fun listFruitsNameStartingWith(search: String): List<Fruit> {
        val fruits = fruitRepository.findByNameStartingWith(name = search)
        if (fruits.isEmpty()) {
            throw Exception("not found")
        }

        return fruits
    }

    @Transactional(readOnly = true)
    fun listFruitsNameEndingWith(search: String): List<Fruit> {
        val fruits = fruitRepository.findByNameEndingWith(name = search)
        if (fruits.isEmpty()) {
            throw Exception("not found")
        }

        return fruits
    }

    @Transactional(readOnly = true)
    fun listFruitsOrderByNameAscAndOriginDesc(): List<Fruit> {
        val fruits = fruitRepository.findAllByOrderByNameAscOriginDesc()
        if (fruits.isEmpty()) {
            throw Exception("not found")
        }

        return fruits
    }

    @Transactional(readOnly = true)
    fun existsFruitByName(name: String): Boolean {
        return fruitRepository.existsByName(name = name)
    }

    @Transactional(readOnly = true)
    fun countFruitsByName(name: String): Long {
        return fruitRepository.countByName(name = name)
    }

    @Transactional(readOnly = true)
    fun listFruitsPageable(origin: String, pageable: Pageable): Page<Fruit> {
        return fruitRepository.findByOrigin(
            origin = origin,
            pageable = pageable
        )
    }

    @Transactional(rollbackFor = [Exception::class])
    fun deleteFruit(id: Long) {
        fruitRepository.deleteById(id)
    }

    @Transactional(rollbackFor = [Exception::class])
    fun softDeleteFruit(id: Long) {
        val fruit = fruitRepository.findByIdOrNull(id = id) ?: throw Exception("not found")

        fruit.isDeleted = true
        fruit.updatedAt = ZonedDateTime.now()

        fruitRepository.save(fruit)
    }
}