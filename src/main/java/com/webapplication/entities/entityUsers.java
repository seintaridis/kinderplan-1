package com.webapplication.entities;

import java.sql.Timestamp;
import java.util.Collection;

/**
 * Created by dimitris on 5/16/2017.
 */
@Entity
@Table(name = "Users", schema = "public", catalog = "kinderplan_db")
public class entityUsers {
    private Integer id;
    private String email;
    private String password;
    private Integer category;
    private Integer userId;
    private Boolean validated;
    private Timestamp dateCreated;
    private Timestamp lastLogin;
    private Collection<entityParents> parentssById;
    private Collection<entityProviders> providerssById;

    @Id
    @Column(name = "id", nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "email", nullable = false, length = -1)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "password", nullable = false, length = -1)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "category", nullable = false)
    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    @Basic
    @Column(name = "user_id", nullable = false)
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "validated", nullable = false)
    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    @Basic
    @Column(name = "date_created", nullable = false)
    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Basic
    @Column(name = "last_login", nullable = false)
    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        entityUsers that = (entityUsers) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (validated != null ? !validated.equals(that.validated) : that.validated != null) return false;
        if (dateCreated != null ? !dateCreated.equals(that.dateCreated) : that.dateCreated != null) return false;
        if (lastLogin != null ? !lastLogin.equals(that.lastLogin) : that.lastLogin != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (validated != null ? validated.hashCode() : 0);
        result = 31 * result + (dateCreated != null ? dateCreated.hashCode() : 0);
        result = 31 * result + (lastLogin != null ? lastLogin.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "usersByUserId")
    public Collection<entityParents> getParentssById() {
        return parentssById;
    }

    public void setParentssById(Collection<entityParents> parentssById) {
        this.parentssById = parentssById;
    }

    @OneToMany(mappedBy = "usersByUserId")
    public Collection<entityProviders> getProviderssById() {
        return providerssById;
    }

    public void setProviderssById(Collection<entityProviders> providerssById) {
        this.providerssById = providerssById;
    }
}
