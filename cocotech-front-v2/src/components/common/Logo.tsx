/**
 * Componente Logo de CocoTech.
 * Combinación de ícono Tabler-style + nombre, con tamaño configurable.
 */
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCartShopping } from "@fortawesome/free-solid-svg-icons";

interface Props {
  size?: "sm" | "md" | "lg";
  color?: string;
  showText?: boolean;
}

const Logo = ({ size = "md", color, showText = true }: Props) => {
  const sizes = {
    sm: { icon: 18, text: 14 },
    md: { icon: 24, text: 18 },
    lg: { icon: 36, text: 28 },
  };
  const { icon, text } = sizes[size];

  return (
    <div
      style={{
        display: "inline-flex",
        alignItems: "center",
        gap: "8px",
        color: color ?? "var(--coco-primary)",
        fontWeight: 600,
        fontSize: `${text}px`,
      }}
    >
      <FontAwesomeIcon icon={faCartShopping} style={{ fontSize: `${icon}px` }} />
      {showText && <span>CocoTech</span>}
    </div>
  );
};

export default Logo;
