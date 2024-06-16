<div align="center">
  <h1 align="center">AI News App</h1>
  <a target="_blank" href="https://www.producthunt.com/posts/papermark-3?utm_source=badge-top-post-badge&amp;utm_medium=badge&amp;utm_souce=badge-papermark">
    <img src="app/src/main/res/drawable-nodpi/logo.webp" alt="LoginGif" width="80" height="70">
  </a>
</div>

<br/>

<div align="center">
  <img src="https://img.shields.io/badge/version-1.0-green"> 
  <img src="https://img.shields.io/badge/kotlin-1.9.23-blueviolet"> 
  <img src="https://img.shields.io/badge/compose-1.6.7-blueviolet"> 
  <img src="https://img.shields.io/badge/gradle-8.8-blueviolet">
  <img src="https://img.shields.io/badge/clean architecture-red">
  <img src="https://img.shields.io/badge/DI-orange"> 
</div>

<br/>

ZAI News — это новостное мобильное приложение с AI в открытом доступе, которое использует RSS каналы, а так же Telegram каналы. Основная цель этого приложения — стать ведущим примером создания современных приложений для всех разработчиков Android.

Идея состоит в том, чтобы сделать многомодульное приложение, демонстрируя при этом новые библиотеки и инструменты, которые упрощают создание высококачественных приложений для Android.

<br/>

<img src="img/4.png" alt="Start" height="450px">

## Описание модулей:

|  Модуль |     Описание     |
|:----------:|:------------:|
|     app     |   Presentation слой   |
|  data |     Слой данных    |
| domain |     Domain слой    |
|     RSA    | RSA модуль |
| RSS | RSS модуль |
| SpeachTextCompose | Модуль чтения текста в Compose |
| TelegramAPI | TDP Telegram модуль |
| TranslateML | Модуль переводчика |

## Список используемых библиотек:
- Kotlin
- Coroutines, Flow
- Compose, Compose Navigation
- Glance, Glanse Material3 (App Widget)
- Dagger2, Hilt
- KSP
- Retrofit, Gson
- Room
- Paging
- Google ML Translate
- TDP Api
- Coil Compose
- Hidden Secrets (C++) Plugin
- Jsoup
- Kotlin Reflect

--------------------

### Скриншоты работы приложения:

<p float="left">
  <img src="img/1.png" alt="Start" height="350px">
  <img src="img/2.png" alt="Start" height="350px">
  <img src="img/3.png" alt="Start" height="350px">
</p>

<p float="left">
  <img src="img/5.png" alt="Start" height="350px">
  <img src="img/6.png" alt="Start" height="350px">
  <img src="img/7.png" alt="Start" height="350px">
</p>

--------------------

## License

The MIT License (MIT)

Copyright (c) 2024 Anton Zaitsev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
