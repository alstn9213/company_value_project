# Docker

- 실행: `docker-compose up -d`
- 완전 종료 (Down) : `docker-compose down`
- 일시 정지 (Stop): `docker-compose stop`
- Redis 비우기 (필수): `docker exec -it company_redis redis-cli flushall`
- 스프링부트 서버 로그 보기: `docker logs -f company_backend`
- 스프링부트 코드 수정후 다시 빌드 시: `docker-compose up -d --build`

기존 코드가 만들어낸 '잘못된 데이터(ResponseEntity JSON)'가 Redis에 남아있으면, 새 코드가 실행돼도 에러가 날 수 있으니 Redis를 꼭 비워야한다.

## --build를 붙이는 이유

- MariaDB / Redis (다른 사람이 만들어둔 프로그램):
  이건 이미 누군가가 잘 만들어둔 완성품(이미지)을 인터넷(Docker Hub)에서 다운로드만 받아서 실행하면 된다. 따라서 빌드(Build) 과정이 필요 없다.

- Spring Boot(내가 만든 프로그램): 이건 다운로드 받는 게 아니라, 내 컴퓨터에 있는 JAR 파일(코드)을 가지고 즉석에서 이미지를 "제조(Build)" 해야 한다. --build 옵션은 "기존에 만들어둔 게 있더라도 무시하고, 최신 코드(JAR)를 넣어서 새로 제조하라는 뜻이다.

- 언제 --build를 붙여야 할까?
  - 자바 코드를 수정하고 다시 JAR를 만들었을 때(새 코드를 반영해야 하니까)
  - 안 붙여도 될 때 (docker-compose up -d): 코드는 안 건드리고 그냥 껐다가 다시 켤 때, 혹은 DB 설정만 바꿨을 때.

## 종료

- 작업이 끝났다면 도커를 정리하자.

- 상황에 따라 두 가지 방법 중 하나를 선택.

1. 일시 정지 (Stop): `docker-compose stop`

- 데이터 보존, 켜기 빠름

- 작업을 잠시 멈추거나 내일 다시 할 예정이라면, 컨테이너를 삭제하지 않고 멈추기만 하는 것이 좋다.

- 특징:
  - 실행 중인 프로그램(Spring Boot, DB, Redis)만 종료된다.
  - 컨테이너 설정이 그대로 남아있어 나중에 다시 켤 때(start 또는 up) 속도가 매우 빠르다.

2. 완전 종료 (Down) : `docker-compose down`

- 깔끔한 정리

- 프로젝트 시연이 완전히 끝났거나, 설정을 꼬여서 초기화하고 싶을 때 사용.

- 특징:
  - 컨테이너를 삭제하고 네트워크 연결도 끊는다.
  - 다시 실행하려면(up) 컨테이너를 처음부터 다시 생성해야 하므로 시간이 조금 더 걸린다.

주의: volumes 설정 덕분에 DB 데이터(db_data 폴더)는 안전하지만, 컨테이너 내부의 임시 데이터는 사라진다.

- DB 데이터까지 싹 다 지우고 초기화: `docker-compose down -v`
