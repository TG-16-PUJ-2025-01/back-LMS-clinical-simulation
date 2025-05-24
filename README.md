# back-LMS-clinical-simulation

.
├── src/
│   ├── config/
│   │   ├── security/
│   │   │   ├── SecurityConfig.java
│   │   │   ├── JwtAutheticationFilter.java
│   │   │   └── PasswordGenerator.java
│   │   ├── docs/
│   │   │   └── SwaggerConfig.java
│   │   ├── handlers/
│   │   │   └── GlobalExceptionHandler.java
│   │   └── data/
│   │       └── DBInitializer.java
│   ├── shared/
│   │   ├── dtos/
│   │   │   ├── ApiResponseDto.java
│   │   │   ├── ErrorDto.java
│   │   │   └── ValidationErrorDto.java
│   │   └── services/
│   │       └── CrudService.java (to fix)
│   ├── accounts (users + auth)/
│   │   ├── controllers/
│   │   ├── services/
│   │   ├── repositories/
│   │   ├── dtos/
│   │   └── models/
│   ├── practices (practices + simulations)/
│   │   ├── controllers/
│   │   ├── services/
│   │   ├── repositories/
│   │   ├── dtos/
│   │   └── models/
│   ├── subjects (courses + classes)/
│   │   ├── controllers/
│   │   ├── services/
│   │   ├── repositories/
│   │   ├── dtos/
│   │   └── models/
│   ├── booking (rooms + calendar management)/
│   │   ├── controllers/
│   │   ├── services/
│   │   ├── repositories/
│   │   ├── dtos/
│   │   └── models/
│   ├── grades (grades info + grade books / rubrics)/
│   │   ├── controllers/
│   │   ├── services/
│   │   ├── repositories/
│   │   ├── dtos/
│   │   └── models/
│   └── videos (streaming + videos)/
│       ├── controllers/
│       ├── services/
│       ├── repositories/
│       ├── dtos/
│       └── models/
└── tests/
    ├── integration/
    └── services/
