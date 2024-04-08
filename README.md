# Konotop

> [!CAUTION]
> This is just proof of concept. Far away from production readiness

## Description

Retrofit-like HTTP client library for kotlin multi platform. Based on ktor, ksp, kotlin compiler plugin  

## Roadmap
- Request mapping
  - [x] Basic 
    - [x] `@GET`, `@POST`, `@PUT`, `@DELETE`, `@HEAD`, `@OPTIONS`,
    - [x] `@Path`
    - [x] `@Query`
    - [x] `@Body`
  - [ ] Headers
    - [ ] `@Header`
    - [ ] `@Headers`
  - [ ] Multipart
    - [ ] `@Multipart`
    - [ ] `@Part`
  - [ ] Form like requests
    - [ ] `@Field` 
    - [ ] `@FormUrlEncoded`
  - [ ] Other
    - [ ] `@QueryMap`
    - [ ] `@HeaderMap`
    - [ ] `@FieldMap`
    - [ ] `@PartMap`
    - [ ] `@QueryName`
    - [ ] `@Url`
- Response mapping
  - [ ] `Response<T>`
  - [ ] Error handling
- Test coverage
  - [ ] Local HTTP server
  - [ ] KSP tests
- Publishing
  - [ ] Gradle plugin
  - [ ] Publish!