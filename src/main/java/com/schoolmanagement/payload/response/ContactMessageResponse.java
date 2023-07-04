package com.schoolmanagement.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ContactMessageResponse implements Serializable {
    // "Serializable" bir terimdir ve genellikle programlama dilleriyle ilgili olarak kullanılır.
    // Bir nesnenin "serializable" olarak işaretlenmesi, o nesnenin bir veri akışına veya veritabanına
    // kaydedilebileceği, başka bir programda yeniden oluşturulabileceği ve aynı şekilde kullanılabileceği
    // anlamına gelir. Bu, nesnelerin veri tabanları veya depolama cihazları arasında taşınmasını kolaylaştırır
    // ve farklı uygulamalar arasında veri paylaşımını daha da kolaylaştırır. Serializable işaretleme
    // genellikle bir dilin standart kütüphaneleri tarafından sağlanır ve büyük ölçüde nesne
    // serileştirme sürecini otomatikleştirir.

    private String name;

    private String email;

    private String subject;

    private String message;

    private LocalDate date;
}
/*              ***** @Builder(toBuilder = true) annotation *****

@Builder(toBuilder = true) annotation, Lombok kütüphanesinin bir parçasıdır ve Java sınıflarını Builder tasarım
desenine uygun hale getirmek için kullanılır. Bu annotation sayesinde otomatik olarak bir Builder sınıfı
oluşturulur ve nesne oluşturma sürecini kolaylaştırır.

Normalde bir sınıfı Builder desenine uygun hale getirmek için, sınıfa ait alanları belirli bir sırayla atanabilen
setter metodları ve nesne oluşturmayı sağlayan bir constructor metodu eklemek gerekmektedir.
Bu işlemler zaman alıcı ve hata yapma olasılığını artırıcı olabilir. @Builder(toBuilder = true) annotation
kullanıldığında ise Lombok otomatik olarak bu işlemleri gerçekleştirir.

toBuilder = true parametresi, oluşturulan Builder sınıfının bir nesne üzerinden çağrıldığında, o nesnenin
değerlerini kopyalayarak yeni bir Builder nesnesi oluşturmasını sağlar. Bu özellik, mevcut bir nesnenin
değerlerini temel alarak değişiklik yapmak ve yeni bir nesne oluşturmak için kullanışlıdır.

Aşağıda @Builder(toBuilder = true) annotation kullanılarak oluşturulan bir örneğin nasıl kullanılabileceği
gösterilmektedir:

import lombok.Builder;

@Builder(toBuilder = true)
public class Person {
    private String name;
    private int age;
}

// Kullanımı:

Person person1 = Person.builder()
                        .name("John")
                        .age(25)
                        .build();

Person person2 = person1.toBuilder()
                        .name("Alice")
                        .build();

Yukarıdaki örnekte, Person sınıfı @Builder(toBuilder = true) annotation ile işaretlenmiştir.
Bu sayede Person sınıfından bir nesne oluşturmak için Builder kullanılabilir hale gelir. person1
nesnesi oluşturulduktan sonra toBuilder() metodu çağrılarak person1'in değerlerini temel alan yeni bir
Builder nesnesi oluşturulur. Daha sonra bu Builder nesnesi üzerinden name alanının değeri değiştirilerek
person2 nesnesi oluşturulur. (age ayni (25) olarak kalir)
 */


/*             ***** implements Serializable ifadesi *****

ChatGPT
implements Serializable ifadesi, bir sınıfın Java Serialization işlemine tabi tutulabileceğini belirtmek için
kullanılır. Serialization, bir nesnenin bellekten disk veya ağ üzerinden taşınabilir bir formata dönüştürülmesini
sağlar ve bu şekilde nesne durumunun korunmasını ve iletilmesini sağlar.

Bir sınıfın Serializable arayüzünü uygulaması, o sınıfın nesnelerinin serileştirilebilir ve deserileştirilebilir
olmasını sağlar. Serileştirilebilir sınıflar, nesnelerinin baytlara dönüştürülerek depolanması, aktarılması ve
daha sonra tekrar nesne haline getirilmesi işlemlerine izin verir.

Bu durumda ContactMessageResponse sınıfına implements Serializable ifadesi eklenmesinin nedeni, bu sınıfın
nesnelerinin serileştirilebilir olmasını sağlamaktır. Bu, ContactMessageResponse sınıfının nesnelerinin baytlara
dönüştürülerek depolanmasına, ağ üzerinde iletilmesine veya başka bir işleme tabi tutulmasına olanak tanır.Örneğin,
nesnelerin veritabanına kaydedilmesi veya bir dosyaya yazılması gibi durumlar için serileştirme önemli olabilir.

Serializable arayüzü işaretlenmiş sınıfların serileştirme sürecinde belirli kurallara uyması gerektiğini
unutmayın. Serileştirilebilir sınıfların tüm alanları serileştirilebilir olmalı ve sınıfın tüm üst sınıfları ve
içeğine sahip nesneler de serileştirilebilir olmalıdır.


User
implements Serializable  kullanmasak da oluyormus, kullanip kullanmamak arasindaki fark nedir, yani ne avantaj saglar

ChatGPT
Evet, implements Serializable kullanmadan da bir sınıfın serileştirilebilir olması mümkündür. Ancak implements
Serializable ifadesini eklemek bazı avantajlar sağlar:

Nesne Durumunun Korunması: Serializable arayüzünü uygulayan bir sınıfın nesneleri, serileştirme işlemi ile
bellekten disk veya ağ üzerine taşınabilir bir formata dönüştürülebilir. Bu sayede nesne durumu korunur ve
ileride tekrar geri yüklenerek kullanılabilir.

Veri Depolama ve Transferi: Serileştirme, nesneleri baytlara dönüştürerek veri depolama veya transferi için
kullanılmasını sağlar. Serileştirilmiş nesneleri dosyalara yazabilir, veritabanına kaydedebilir veya ağ üzerinde
aktarabilirsiniz.

Uzaktan İletişim: Serileştirme, uzaktan iletişim senaryolarında önemli bir rol oynar. Nesneleri serileştirerek,
nesneleri bir ağ üzerinden başka bir makineye iletebilir ve orada tekrar kullanabilirsiniz. Örneğin, bir
istemci-sunucu uygulamasında istemci ve sunucu arasında nesnelerin iletilmesi için serileştirme kullanılabilir.

Çerçeveler ve Kütüphaneler ile Entegrasyon: Birçok Java çerçevesi ve kütüphanesi, serileştirme özelliğini
kullanarak nesneleri depolamak, paylaşmak veya iletişim kurmak için kullanabilir. Bu çerçevelerin ve
kütüphanelerin, nesneleri serileştirebilmesi için sınıfların Serializable arayüzünü uygulaması gerekebilir.

Özetle, implements Serializable ifadesini kullanmak, bir sınıfın serileştirme yeteneğini kazanmasını sağlar ve
nesne durumunun korunmasına, veri depolama ve transferine, uzaktan iletişime ve çerçevelerle entegrasyona olanak
tanır. Ancak, serileştirme kullanmadan da bazı alternatif veri aktarım yöntemleri veya dışa aktarma yöntemleri
kullanabilirsiniz.


User
implements Serializable kullanmayinca veri iletimi serilestirmeden mi yapiliyor

ChatGPT
Evet, implements Serializable kullanmadan da veri iletimi yapılabilir. Serileştirme, yalnızca nesnelerin
baytlara dönüştürülerek depolanması, aktarılması veya başka bir şekilde işlenmesi için bir yöntemdir.
Ancak veri iletimi için serileştirme zorunlu değildir ve alternatif yöntemler mevcuttur.

Bir sınıfın implements Serializable ifadesini kullanmaması durumunda, o sınıfın nesneleri serileştirilemez.
Ancak veri iletimi için başka yöntemler kullanılabilir. Örneğin, nesne verilerini metin tabanlı bir formatta
(JSON veya XML gibi) taşıyabilir veya özel bir veri yapısıyla kodlanmış baytlar olarak iletebilirsiniz. Bu durumda,
veriyi alıcı tarafında uygun şekilde işleyebilen bir kodlama ve kod çözme mekanizması kullanmanız gerekebilir.

Veri iletimi için serileştirmeyi kullanmanın avantajlarından biri, Java'nın serileştirme mekanizmasının yerleşik
olarak sunulması ve kullanımının kolay olmasıdır. Ancak bazı durumlarda, serileştirmenin uygun olmadığı veya
performans açısından tercih edilmeyebileceği durumlar olabilir. Bu durumda, farklı veri iletim yöntemlerini
değerlendirmek ve uygulamak gerekebilir.
 */