
export function SidebarItem({ label, icon, activeIcon, isActive, onClick }) {
  
  
  const iconeParaMostrar = (isActive && activeIcon) ? activeIcon : icon;

  return (
    <div 
      className={`menu-item ${isActive ? "active" : ""}`} 
      onClick={onClick}
    >
      <img src={iconeParaMostrar} alt={label} />
      
      <span>{label}</span>
    </div>
  );
}