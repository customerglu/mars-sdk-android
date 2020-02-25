package ai.marax.android.sdk.core;

public class MarsTraitsBuilder {
    private String city;

    public MarsTraitsBuilder setCity(String city) {
        this.city = city;
        return this;
    }

    private String country;

    public MarsTraitsBuilder setCountry(String country) {
        this.country = country;
        return this;
    }

    private String postalCode;

    public MarsTraitsBuilder setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    private String state;

    public MarsTraitsBuilder setState(String state) {
        this.state = state;
        return this;
    }

    private String street;

    public MarsTraitsBuilder setStreet(String street) {
        this.street = street;
        return this;
    }

    private String age;

    public MarsTraitsBuilder setAge(int age) {
        this.age = Integer.toString(age);
        return this;
    }

    private String birthDay;

    public MarsTraitsBuilder setBirthDay(String birthDay) {
        this.birthDay = birthDay;
        return this;
    }

    private String companyName;

    public MarsTraitsBuilder setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    private String companyId;

    public MarsTraitsBuilder setCompanyId(String companyId) {
        this.companyId = companyId;
        return this;
    }

    private String industry;

    public MarsTraitsBuilder setIndustry(String industry) {
        this.industry = industry;
        return this;
    }

    private String createdAt;

    public MarsTraitsBuilder setCreateAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    private String description;

    public MarsTraitsBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    private String email;

    public MarsTraitsBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    private String firstName;

    public MarsTraitsBuilder setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    private String gender;

    public MarsTraitsBuilder setGender(String gender) {
        this.gender = gender;
        return this;
    }

    private String id;

    public MarsTraitsBuilder setId(String id) {
        this.id = id;
        return this;
    }

    private String lastName;

    public MarsTraitsBuilder setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    private String name;

    public MarsTraitsBuilder setName(String name) {
        this.name = name;
        return this;
    }

    private String phone;

    public MarsTraitsBuilder setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    private String title;

    public MarsTraitsBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    private String userName;

    public MarsTraitsBuilder setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public MarsTraits build() {
        return new MarsTraits(
                new MarsTraits.Address(
                        this.city,
                        this.country,
                        this.postalCode,
                        this.state,
                        this.street
                ),
                this.age,
                this.birthDay,
                new MarsTraits.Company(
                        this.companyName,
                        this.companyId,
                        this.industry
                ),
                this.createdAt,
                this.description,
                this.email,
                this.firstName,
                this.gender,
                this.id,
                this.lastName,
                this.name,
                this.phone,
                this.title,
                this.userName
        );
    }
}
