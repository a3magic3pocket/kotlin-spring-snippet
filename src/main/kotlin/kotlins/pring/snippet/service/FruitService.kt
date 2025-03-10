package kotlins.pring.snippet.service

import kotlins.pring.snippet.dto.FruitFileDto
import kotlins.pring.snippet.dto.FruitReqDto
import kotlins.pring.snippet.dto.FruitReqFormDataDto
import kotlins.pring.snippet.dto.FruitSizeDto
import kotlins.pring.snippet.entity.Fruit
import kotlins.pring.snippet.repository.FruitRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

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
    fun updateFruit(fruitId: Long, fruitReqDto: FruitReqDto): Fruit {
        val savedFruit = fruitRepository.findByIdOrNull(id = fruitId) ?: throw Exception("not found")

        savedFruit.origin = fruitReqDto.origin
        savedFruit.name = fruitReqDto.name

        return fruitRepository.save(savedFruit)
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
    fun listFruitByName(name: String): List<Fruit> {
        val fruits = fruitRepository.findByName(name = name)
        if (fruits.isEmpty()) {
            throw Exception("not found")
        }

        return fruits
    }
}