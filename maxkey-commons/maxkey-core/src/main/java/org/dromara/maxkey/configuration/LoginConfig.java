/*
 * Copyright [2020] [MaxKey of copyright http://www.maxkey.top]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 

package org.dromara.maxkey.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoginConfig {
    
    @Value("${maxkey.login.captcha:true}")
    boolean captcha;
    
    @Value("${maxkey.login.captcha.type:TEXT}")
    String captchaType;
    
    @Value("${maxkey.login.mfa:false}")
    boolean mfa;
    
    @Value("${maxkey.login.kerberos:false}")
    boolean kerberos;
    
    @Value("${maxkey.login.remeberme:false}")
    boolean remeberMe;
    
    @Value("${maxkey.login.wsfederation:false}")
    boolean wsFederation;
    
    @Value("${maxkey.login.jwt:false}")
    boolean jwt;
    
    @Value("${maxkey.login.registration:false}")
    boolean registration;
    
    @Value("${maxkey.login.cas.serverUrlPrefix:http://localhost:9527/sign/authz/cas}")
    String casServerUrlPrefix;

    @Value("${maxkey.login.cas.service:http://localhost:8526/maxkey-mgt/#/passport/trust/auth}")
    String casService;

    /**
     *  .
     */
    public LoginConfig() {
    }

    public boolean isCaptcha() {
        return captcha;
    }

    public void setCaptcha(boolean captcha) {
        this.captcha = captcha;
    }

    public boolean isKerberos() {
        return kerberos;
    }

    public void setKerberos(boolean kerberos) {
        this.kerberos = kerberos;
    }

    public boolean isMfa() {
        return mfa;
    }

    public void setMfa(boolean mfa) {
        this.mfa = mfa;
    }



    public boolean isRemeberMe() {
        return remeberMe;
    }

    public void setRemeberMe(boolean remeberMe) {
        this.remeberMe = remeberMe;
    }

    public boolean isWsFederation() {
        return wsFederation;
    }

    public void setWsFederation(boolean wsFederation) {
        this.wsFederation = wsFederation;
    }
    
    public String getCasServerUrlPrefix() {
        return casServerUrlPrefix;
    }

    public void setCasServerUrlPrefix(String casServerUrlPrefix) {
        this.casServerUrlPrefix = casServerUrlPrefix;
    }

    public String getCasService() {
        return casService;
    }

    public void setCasService(String casService) {
        this.casService = casService;
    }

    public String getCaptchaType() {
        return captchaType;
    }

    public void setCaptchaType(String captchaType) {
        this.captchaType = captchaType;
    }

    public boolean isJwt() {
		return jwt;
	}

	public void setJwt(boolean jwt) {
		this.jwt = jwt;
	}

	public boolean isRegistration() {
		return registration;
	}

	public void setRegistration(boolean registration) {
		this.registration = registration;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LoginConfig [captcha=");
		builder.append(captcha);
		builder.append(", captchaType=");
		builder.append(captchaType);
		builder.append(", mfa=");
		builder.append(mfa);
		builder.append(", kerberos=");
		builder.append(kerberos);
		builder.append(", remeberMe=");
		builder.append(remeberMe);
		builder.append(", wsFederation=");
		builder.append(wsFederation);
		builder.append(", jwt=");
		builder.append(jwt);
		builder.append(", registration=");
		builder.append(registration);
		builder.append(", casServerUrlPrefix=");
		builder.append(casServerUrlPrefix);
		builder.append(", casService=");
		builder.append(casService);
		builder.append("]");
		return builder.toString();
	}

}
