package com.keb.club_pila.controller;

import com.keb.club_pila.dto.teacher.TeacherDto;
import com.keb.club_pila.model.response.BasicResponse;
import com.keb.club_pila.model.response.CommonResponse;
import com.keb.club_pila.model.response.ErrorResponse;
import com.keb.club_pila.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class TeacherApiController {
    private final TeacherService teacherService;

    /*
    1. 선생님 등록
    2. 선생님 삭제
    3. 선생님 업데이트 (정보)
    3-1. 선생님 사진 업데이트 <- 관리자용
    4. 선생님 읽기 - 모든 선생님, 특정 아이디 선생님 -> 딸린 수업까지 전부 ?

    */
// @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path="/api/v1/admin/teacher",consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
   // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<? extends BasicResponse> save(@RequestPart(value="key", required=false) TeacherDto.TeacherSaveRequestDto teacherSaveRequestDto,
                                                        @RequestPart(value="file", required=true) MultipartFile file){
        System.out.println(teacherSaveRequestDto.toString());
        Long result = teacherService.teacherSave(teacherSaveRequestDto, file);
        return ResponseEntity.created(URI.create("/api/v1/teacher/" + result)).build();
    }
    //선생님 이미지 업로드용
    @PostMapping(
            path = "/api/v1/admin/teacher/{id}/image/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends BasicResponse> uploadTeacherProfileImage(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file){
        teacherService.uploadTeacherProfileImage(id, file);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/v1/admin/teacher/{id}")
    public ResponseEntity<? extends BasicResponse> delete(@PathVariable Long id) {
        boolean result=teacherService.deleteById(id);
        if (!result) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("일치하는 수업 아이디 조회 실패: " + id));
        }
        return ResponseEntity.noContent().build();

    }
  //  @PreAuthorize("hasRole('ROLE_ADMIN')")
    //관리자용 선생님 리스트 쫙
    @GetMapping("/api/v1/admin/teacher")
    public ResponseEntity<? extends BasicResponse> findAllTeachers() {
        List<TeacherDto.TeacherResponseDto> teachers=teacherService.findAllTeachers();
        if (teachers.isEmpty()) {

            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(new CommonResponse<>(teachers));

    }
    //일반 유저용 선생님 리스트 쫙
    @GetMapping("/api/v1/user/teacher")
    public ResponseEntity<? extends BasicResponse> findAllWorkingTeachersSimple(){
        List<TeacherDto.TeacherResponseSimpleDto> teachers=teacherService.findAllWorkingTeachersSimple();
        if (teachers.isEmpty()) {

            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(new CommonResponse<>(teachers));
    }
//get teacher info
    @GetMapping("/api/v1/teacher/{id}")
    public ResponseEntity<? extends BasicResponse> findById(@PathVariable Long id) {
        TeacherDto.TeacherResponseDto teacherResponseDto = teacherService.findById(id);

        if (teacherResponseDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("잘못된 선생님 조회 요청: " + id));
        }
        return ResponseEntity.ok().body(new CommonResponse<TeacherDto.TeacherResponseDto>(teacherResponseDto));

    }

    @PutMapping("/api/v1/admin/teacher/{id}")
    public ResponseEntity<? extends BasicResponse> update(@PathVariable Long id, @RequestBody TeacherDto.TeacherUpdateDto teacherUpdateDto) {
        Long result = teacherService.updateById(id, teacherUpdateDto);
        if (result==0L) { // 실패시
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("잘못된 선생님 갱신 요청: "+id));
        }
        //성공시
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/api/v1/teacher/{id}/download")
    public byte[] downloadTeacherProfileImage(@PathVariable("id") Long id){

  return  teacherService.downloadTeacherProfileImage(id);
    }
}
