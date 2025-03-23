package co.edu.javeriana.lms.booking.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.booking.dtos.RoomDto;
import co.edu.javeriana.lms.booking.dtos.RoomTypeDto;
import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.booking.models.RoomType;
import co.edu.javeriana.lms.booking.services.RoomService;
import co.edu.javeriana.lms.booking.services.RoomTypeService;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.shared.dtos.PaginationMetadataDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.data.domain.Page;

@Slf4j
@RestController
@RequestMapping("/room")
public class RoomController {

	@Autowired
	private RoomService roomService;

	@Autowired
	private RoomTypeService roomTypeService;

	@GetMapping("/all")
	public ResponseEntity<ApiResponseDto<?>> getAllRooms(
			@Min(0) @RequestParam(defaultValue = "0") Integer page,
			@Min(1) @RequestParam(defaultValue = "10") Integer size,
			@RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "true") Boolean asc,
			@RequestParam(defaultValue = "") String filter) {

		log.info("Requesting all rooms");

		Page<Room> roomsPage = roomService.searchRooms(filter, page, size, sort, asc);

		PaginationMetadataDto metadata = new PaginationMetadataDto(page, roomsPage.getNumberOfElements(),
				roomsPage.getTotalElements(), roomsPage.getTotalPages());

		return ResponseEntity.ok(
				new ApiResponseDto<>(HttpStatus.OK.value(), "Rooms retrieved successfully", roomsPage.getContent(),
						metadata));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseDto<?>> getRoomById(@PathVariable Long id) {
		log.info("Requesting room with id={}", id);

		Room room = roomService.findById(id);

		return ResponseEntity
				.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Room retrieved successfully", room, null));
	}

	@GetMapping("/types")
	public ResponseEntity<ApiResponseDto<?>> getRoomTypes() {
		log.info("Requesting all room types");

		List<RoomType> types = roomService.findAllTypes();

		return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Room types retrieved successfully", types,
				null));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseDto<?>> deleteRoomById(@PathVariable Long id) {
		log.info("Deleting room with id={}", id);

		roomService.deleteById(id);

		return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Room deleted successfully", null, null));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponseDto<?>> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomDto roomDto) {
		log.info("Requesting update of room with id={}", id);

		Room updatedRoom = roomService.update(id, roomDto.toEntity());

		return ResponseEntity
				.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Room updated successfully", updatedRoom, null));
	}

	@PostMapping()
	public ResponseEntity<ApiResponseDto<?>> addRoom(@Valid @RequestBody RoomDto roomDto) {
		log.info("Creating room: name={}, typeId={}", roomDto.getName(), roomDto.getTypeId());

		Room newRoom = roomService.save(roomDto.toEntity());

		return ResponseEntity
				.ok(new ApiResponseDto<>(HttpStatus.CREATED.value(), "Room created successfully", newRoom, null));
	}

	@PostMapping("/type")
	public ResponseEntity<ApiResponseDto<?>> addRoomType(@Valid @RequestBody RoomTypeDto typeDto) {
		log.info("Requesting creation of room type: name={}", typeDto.getName());

		RoomType savedType = roomTypeService.save(typeDto.toEntity());

		return ResponseEntity
				.ok(new ApiResponseDto<>(HttpStatus.CREATED.value(), "Room type created successfully", savedType,
						null));
	}

}