import React from "react";
import styles from './footer.module.css';
import { FooterIcon } from "./footer.types"; 
import { FOOTER_ICONS } from "./footerIcons";
import Icon from "../../common/icon/Icon";

const Footer: React.FC = () => {
  return (
    <footer className={styles.footer} aria-label="Footer nav, and copyright information">
      <div className={styles.wrap} >
      <h4 className={styles.info}>More Details about Team 5's project!</h4>
      <hr />
      <ul className={styles.icon}>
        {FOOTER_ICONS.map(({ href, label, id, icon }: FooterIcon) => (
          <li key={id}>
            <a
              href={href}
              target="_blank"
              rel="noopener noreferrer"
              aria-label={label}
            >
              <Icon icon={icon} width={60} height={60} />
            </a>
          </li>
        ))}
      </ul>
      <p className={styles.copyright}>
        &copy; {new Date().getFullYear()}, Team 5 All Rights Reserved.
      </p>
      </div>
    </footer>
  );
};

export default Footer;
