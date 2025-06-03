import React from "react";
import "./Footer.css";
import { useTranslation } from 'react-i18next';

const Footer = () => {
  const { t, i18n } = useTranslation();

  const changeLanguage = (lang) => {
    i18n.changeLanguage(lang);
  };
  return (
    <footer className="footer">
      <h3>{t("footer.title")}</h3>
      <p>{t("footer.description")}</p>
      <div className="social-links">
        <a href="#" target="_blank" rel="noopener noreferrer">
          Facebook
        </a>
        <a href="#" target="_blank" rel="noopener noreferrer">
          Instagram
        </a>
        <a href="#">Email</a>
      </div>
      <div className="language-selector">
        <button onClick={() => changeLanguage("en")}>EN</button>
        <button onClick={() => changeLanguage("uk")}>UK</button>
      </div>
    </footer>
  );
};

export default Footer;
