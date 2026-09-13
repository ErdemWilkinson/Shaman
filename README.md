# Shaman — Yapay Zeka Destekli Mobil Sağlık Ön Değerlendirme Uygulaması

Shaman, kullanıcıların semptomlarını girerek olası hastalıkları ihtimal yüzdeleriyle görebildiği,
Anthropic Claude yapay zeka modeliyle çalışan bir Android sağlık ön değerlendirme uygulamasıdır.
Bu proje, Karadeniz Teknik Üniversitesi Trabzon Meslek Yüksekokulu Bilgisayar Programcılığı
Programı bitirme projesi olarak geliştirilmiştir.

> ⚠️ **Uyarı:** Shaman bir doktor teşhisinin veya klinik muayenenin yerini almaz. Yalnızca
> bireyin doktora gitmeden önce bilinçli bir ön değerlendirme yapmasına yardımcı olan bir
> bilgilendirme aracıdır.

Projenin tam akademik raporuna [`docs/Shaman-Proje-Raporu.pdf`](docs/Shaman-Proje-Raporu.pdf)
dosyasından ulaşabilirsiniz.

## Özellikler

- **Semptom Akışı:** Vücut bölgesi, semptomlar, şikayet süresi, şiddet, aile geçmişi ve yaşam
  tarzı bilgilerini toplayan altı adımlı dinamik soru akışı.
- **Yapay Zeka Analizi:** Toplanan bilgiler Anthropic Claude API'sine gönderilerek olası
  hastalıklar, halk arasında kullanılan Türkçe isimleriyle ve ihtimal yüzdeleriyle listelenir.
- **Hastalık Detayları:** Seçilen hastalık için nedenleri, belirtileri, tedavi önerileri ve
  ne zaman doktora gidilmesi gerektiğini özetleyen detay ekranı.
- **Kullanıcı Hesapları:** Firebase Authentication ile e-posta/şifre tabanlı kayıt ve giriş.
- **Geçmiş Analizler:** Cloud Firestore üzerinde kullanıcıya özel olarak saklanan analiz geçmişi.

## Kullanılan Teknolojiler

| Katman              | Teknoloji                                   |
|---------------------|----------------------------------------------|
| Dil                 | Kotlin                                        |
| Arayüz              | Jetpack Compose, Navigation Compose           |
| Kimlik Doğrulama    | Firebase Authentication                       |
| Veritabanı          | Cloud Firestore (NoSQL)                       |
| Ağ Katmanı          | Retrofit, OkHttp                              |
| Yapay Zeka          | Anthropic Claude API (`claude-haiku-4-5`)     |

## Mimari

```
app/src/main/java/
├── com/erdem/shaman/          # MainActivity, tema
├── network/                   # ClaudeApi, ClaudeRepository, ClaudeRequest
└── ui/
    ├── navigation/             # AppNavigation
    └── screens/                 # Login, Register, SymptomFlow, Result, DiseaseDetail, Profile
```

- **Kullanıcı arayüzü katmanı:** Jetpack Compose ile yazılan ekranlar, Navigation Compose ile
  yönetilen geçişler.
- **Veri katmanı:** Firebase Authentication (kimlik doğrulama) ve Cloud Firestore
  (`users/{userId}/history` alt koleksiyon yapısı ile analiz geçmişi).
- **Yapay zeka entegrasyon katmanı:** Retrofit/OkHttp üzerinden Claude API çağrıları;
  API anahtarı ve zorunlu başlıklar bir OkHttp interceptor ile isteklere eklenir.

## Kurulum

Bu depo, güvenlik nedeniyle hiçbir API anahtarı veya Firebase yapılandırması **içermez**.
Uygulamayı kendi ortamınızda çalıştırmak için aşağıdaki adımları izlemeniz gerekir.

### 1. Depoyu klonlayın

```bash
git clone https://github.com/ErdemWilkinson/Shaman.git
cd Shaman
```

### 2. Firebase yapılandırması

1. [Firebase Console](https://console.firebase.google.com/) üzerinde yeni bir proje oluşturun.
2. Projeye `com.erdem.shaman` paket adıyla bir Android uygulaması ekleyin.
3. Authentication (E-posta/Şifre) ve Cloud Firestore servislerini etkinleştirin.
4. İndirilen `google-services.json` dosyasını `app/` klasörüne kopyalayın
   (şablon için [`app/google-services.json.example`](app/google-services.json.example)
   dosyasına bakabilirsiniz).

### 3. Claude API anahtarı

1. [Anthropic Console](https://console.anthropic.com/) üzerinden bir API anahtarı oluşturun.
2. Proje kök dizinindeki [`local.properties.example`](local.properties.example) dosyasını
   `local.properties` olarak kopyalayın ve `CLAUDE_API_KEY` değerini kendi anahtarınızla
   doldurun:

   ```properties
   sdk.dir=/path/to/Android/Sdk
   CLAUDE_API_KEY=your-anthropic-api-key-here
   ```

   `local.properties` git tarafından takip edilmez; anahtarınız yalnızca yerel makinenizde
   kalır ve derleme sırasında `BuildConfig.CLAUDE_API_KEY` olarak koda enjekte edilir.

### 4. Firestore güvenlik kuralları

Kullanıcıların yalnızca kendi verilerine erişebilmesi için Firestore güvenlik kurallarını
aşağıdaki gibi ayarlamanız önerilir:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
      match /history/{document=**} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
    }
  }
}
```

### 5. Uygulamayı çalıştırın

Android Studio ile projeyi açıp bir emülatör veya fiziksel cihaz üzerinde çalıştırabilir,
ya da komut satırından derleyebilirsiniz:

```bash
./gradlew assembleDebug
```

## Güvenlik Notları

- Bu depoda **hiçbir gerçek API anahtarı veya Firebase kimlik bilgisi bulunmamaktadır.**
- API anahtarları yalnızca `local.properties` (git tarafından yok sayılır) üzerinden
  `BuildConfig` aracılığıyla uygulamaya dahil edilir.
- Üretim ortamı için API anahtarlarının doğrudan mobil istemcide tutulması yerine bir
  arka uç (backend) proxy üzerinden yönetilmesi önerilir.

## Lisans

Bu proje akademik bir bitirme projesi olarak geliştirilmiştir. Kullanım koşulları için
depo sahibiyle iletişime geçiniz.
